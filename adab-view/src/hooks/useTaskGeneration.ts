import { useState, useCallback } from 'react';
import { generateTasksWithBackend, checkTasksExist, deleteTasksByRequirement } from '../api';
import type { TaskCard, Requirement } from '../api';

export const useTaskGeneration = (
  requirements: Requirement[],
  setTaskCards: React.Dispatch<React.SetStateAction<TaskCard[]>>,
  taskCards: TaskCard[]
) => {
  const [generatingTasks, setGeneratingTasks] = useState<Set<number>>(new Set());
  const [generationStatus, setGenerationStatus] = useState<Map<number, string>>(new Map());
  const [batchGenerating, setBatchGenerating] = useState(false);
  const [batchProgress, setBatchProgress] = useState({ current: 0, total: 0 });

  const generateTaskCards = useCallback(async (req: Requirement, index: number) => {
    if (generatingTasks.has(index)) return;

    try {
      setGeneratingTasks(prev => new Set(prev).add(index));
      
      const exists = await checkTasksExist(req.requirementId);
      if (exists.exists && exists.count > 0) {
        if (!confirm(`이미 ${exists.count}개의 과업이 존재합니다. 기존 과업을 삭제하고 새로 생성하시겠습니까?`)) {
          setGeneratingTasks(prev => {
            const next = new Set(prev);
            next.delete(index);
            return next;
          });
          return;
        }
        await deleteTasksByRequirement(req.requirementId);
        setTaskCards(prev => prev.filter(t => t.parentRequirementId?.trim() !== req.requirementId?.trim()));
      }

      generateTasksWithBackend(
        { ...req, index },
        (message) => {
          setGenerationStatus(prev => new Map(prev).set(index, message));
        },
        (task) => {
          setTaskCards(prev => [...prev, { ...task, isNew: true }]);
        },
        () => {
          setGenerationStatus(prev => {
            const next = new Map(prev);
            next.delete(index);
            return next;
          });
          setGeneratingTasks(prev => {
            const next = new Set(prev);
            next.delete(index);
            return next;
          });
          
          setTimeout(() => {
            const rowElement = document.getElementById(`row-${index}`);
            if (rowElement) {
              rowElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
          }, 100);
        },
        (error) => {
          console.error('과업 생성 실패:', error);
          alert(`과업 생성 실패: ${error.message}`);
          setGeneratingTasks(prev => {
            const next = new Set(prev);
            next.delete(index);
            return next;
          });
        }
      );
    } catch (err: unknown) {
      console.error('과업 생성 요청 실패:', err);
      setGeneratingTasks(prev => {
        const next = new Set(prev);
        next.delete(index);
        return next;
      });
    }
  }, [generatingTasks, setTaskCards]);

  const generateSequentialTasks = useCallback(async () => {
    if (batchGenerating) return;

    const requirementsWithTasks = requirements.filter(req => 
      taskCards.some(t => t.parentRequirementId?.trim() === req.requirementId?.trim())
    );

    let visibleRequirements = [...requirements];
    
    if (requirementsWithTasks.length > 0) {
      const choice = confirm(
        `일부 요구사항(${requirementsWithTasks.length}/${requirements.length})에 이미 과업이 존재합니다.\n\n` +
        `[확인]: 기존 과업을 모두 삭제하고 '전체 새로 생성'합니다.\n` +
        `[취소]: 과업이 없는 요구사항만 '건너뛰고 생성'합니다.`
      );

      if (!choice) {
        visibleRequirements = requirements.filter(req => 
          !taskCards.some(t => t.parentRequirementId?.trim() === req.requirementId?.trim())
        );
        
        if (visibleRequirements.length === 0) {
          alert('✅ 모든 요구사항에 이미 과업이 생성되어 있습니다.');
          return;
        }
      }
    }

    setBatchGenerating(true);
    setBatchProgress({ current: 0, total: visibleRequirements.length });

    for (let i = 0; i < visibleRequirements.length; i++) {
      const req = visibleRequirements[i];
      const originalIndex = requirements.findIndex(r => r.requirementId === req.requirementId);

      try {
        setBatchProgress({ current: i + 1, total: visibleRequirements.length });
        setGeneratingTasks(prev => new Set(prev).add(originalIndex));

        // Auto-focus current requirement
        setTimeout(() => {
          const rowElement = document.getElementById(`row-${originalIndex}`);
          if (rowElement) {
            rowElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
          }
        }, 50);

        const exists = await checkTasksExist(req.requirementId);
        if (exists.exists && exists.count > 0) {
          await deleteTasksByRequirement(req.requirementId);
          setTaskCards(prev => prev.filter(t => t.parentRequirementId?.trim() !== req.requirementId?.trim()));
        }

        await new Promise<void>((resolve, reject) => {
          generateTasksWithBackend(
            { ...req, index: originalIndex },
            (message) => {
              setGenerationStatus(prev => new Map(prev).set(originalIndex, message));
            },
            (task) => {
              setTaskCards(prev => [...prev, { ...task, isNew: true }]);
            },
            () => {
              setGenerationStatus(prev => {
                const next = new Map(prev);
                next.delete(originalIndex);
                return next;
              });

              setGeneratingTasks(prev => {
                const next = new Set(prev);
                next.delete(originalIndex);
                return next;
              });

              setTimeout(() => {
                const rowElement = document.getElementById(`row-${originalIndex}`);
                if (rowElement) {
                  rowElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
              }, 100);

              resolve();
            },
            (error: Error) => {
              console.error(`요구사항 ${req.requirementId} 과업 생성 실패:`, error);
              setGeneratingTasks(prev => {
                const next = new Set(prev);
                next.delete(originalIndex);
                return next;
              });
              reject(error);
            }
          );
        });

        if (i < visibleRequirements.length - 1) {
          await new Promise(resolve => setTimeout(resolve, 800));
        }

      } catch (error) {
        console.error(`요구사항 ${req.requirementId} 처리 중 오류:`, error);
      }
    }

    setBatchGenerating(false);
    setBatchProgress({ current: 0, total: 0 });
    alert('✅ 순차적 과업 생성이 완료되었습니다!');
  }, [batchGenerating, requirements, taskCards, setTaskCards]);

  return {
    generatingTasks,
    generationStatus,
    batchGenerating,
    batchProgress,
    generateTaskCards,
    generateSequentialTasks
  };
};
