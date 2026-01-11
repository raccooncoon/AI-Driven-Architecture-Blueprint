import { useState, useEffect, useRef } from 'react';
import { getCurrentModelName, uploadRequirementsBatch, updateRequirement } from './api';
import Settings from './Settings';
import { useRequirements } from './hooks/useRequirements';
import { useTaskGeneration } from './hooks/useTaskGeneration';
import { Header } from './components/Header';
import { BatchProgress } from './components/BatchProgress';
import { RequirementCard } from './components/RequirementCard';

function App() {
  // --- States ---
  const [showSettings, setShowSettings] = useState(false);
  const [currentModel, setCurrentModel] = useState('');
  const [collapsedTaskViews, setCollapsedTaskViews] = useState<Set<string>>(new Set());
  const [uploading, setUploading] = useState(false);
  const [leftWidth, setLeftWidth] = useState((window.innerWidth - 100) * 0.45);
  const [isResizing, setIsResizing] = useState(false);

  // Editing States
  const [editingIndex, setEditingIndex] = useState<number | null>(null);
  const [editingContent, setEditingContent] = useState({
    requestContent: '',
    deadline: '',
    implementationOpinion: '',
    pobaOpinion: '',
    techInnovationOpinion: ''
  });

  const fileInputRef = useRef<HTMLInputElement>(null);

  // --- Hooks ---
  const {
    requirements = [],
    setRequirements,
    taskCards = [],
    setTaskCards,
    loading,
    error,
    refetch
  } = useRequirements();

  const {
    generatingTasks,
    generationStatus,
    batchGenerating,
    batchProgress,
    generateTaskCards,
    generateSequentialTasks
  } = useTaskGeneration(requirements, setTaskCards, taskCards);

  // --- Effects ---
  useEffect(() => {
    const fetchModel = async () => {
      try {
        const model = await getCurrentModelName();
        setCurrentModel(model);
      } catch (err) {
        console.error('모델명 로딩 실패:', err);
      }
    };
    fetchModel();
  }, []);


  useEffect(() => {
    if (!isResizing) return;

    const handleMouseMove = (e: MouseEvent) => {
      const newWidth = Math.max(300, Math.min(2000, e.clientX - 32)); 
      setLeftWidth(newWidth);
    };

    const handleMouseUp = () => {
      setIsResizing(false);
      document.body.style.cursor = 'default';
      document.body.style.userSelect = 'auto';
    };

    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);
    
    document.body.style.cursor = 'col-resize';
    document.body.style.userSelect = 'none';

    return () => {
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
    };
  }, [isResizing]);

  // --- Handlers ---
  const handleFileUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    try {
      setUploading(true);
      const result = await uploadRequirementsBatch(file);
      alert(`✅ ${result.message || '업로드 성공!'}\n${result.count}건의 요구사항이 저장되었습니다.`);
      refetch();
    } catch (err: any) {
      console.error('파일 업로드 실패:', err);
      alert(`❌ 업로드 실패: ${err.response?.data?.message || err.message}`);
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  const startEditing = (index: number, req: any) => {
    setEditingIndex(index);
    setEditingContent({
      requestContent: req.requestContent,
      deadline: req.deadline || '',
      implementationOpinion: req.implementationOpinion || '',
      pobaOpinion: req.pobaOpinion || '',
      techInnovationOpinion: req.techInnovationOpinion || ''
    });
  };

  const saveEditing = async (req: any, index: number) => {
    try {
      const updatedReq = { ...req, ...editingContent };
      await updateRequirement(req.requirementId, updatedReq);
      setRequirements(prev =>
        prev.map((r, i) => i === index ? updatedReq : r)
      );
      setEditingIndex(null);
      alert('✅ 수정이 완료되었습니다.');
    } catch (err: any) {
      console.error('수정 실패:', err);
      alert(`❌ 수정 실패: ${err.response?.data?.message || err.message}`)
    }
  };

  const toggleTaskView = (id: string) => {
    setCollapsedTaskViews(prev => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  // --- Render ---
  if (showSettings) {
    return <Settings onBack={() => {
      setShowSettings(false);
      getCurrentModelName().then(setCurrentModel);
    }} />;
  }

  if (loading) {
    return (
      <div style={{ width: '100vw', height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: '#1e293b' }}>
        <div style={{ background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)', color: 'white', padding: '2rem 3rem', borderRadius: '16px', fontSize: '1.1rem', fontWeight: 600, boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)' }}>
          🚀 ADAB 로딩 중...
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ width: '100vw', height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: '#1e293b' }}>
        <div style={{ background: 'linear-gradient(135deg, #ef4444 0%, #b91c1c 100%)', color: 'white', padding: '2rem 3rem', borderRadius: '16px', fontSize: '1.1rem', fontWeight: 600, boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)', textAlign: 'center' }}>
          ❌ 에러 발생<br/>
          <span style={{ fontSize: '0.9rem', opacity: 0.9, marginTop: '0.5rem', display: 'block' }}>{error}</span>
          <button onClick={() => refetch()} style={{ marginTop: '1.5rem', padding: '0.5rem 1rem', background: 'white', color: '#b91c1c', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 700 }}>다시 시도</button>
        </div>
      </div>
    );
  }

  return (
    <div style={{ width: '100vw', height: '100vh', display: 'flex', flexDirection: 'column', backgroundColor: '#0f172a', color: '#f8fafc', fontFamily: "'Pretendard', -apple-system, sans-serif" }}>
      <Header
        currentModel={currentModel}
        onOpenSettings={() => setShowSettings(true)}
      />

      <main style={{ flex: 1, padding: '1.5rem', overflow: 'auto', backgroundColor: '#1e293b' }}>
        {(requirements || []).length === 0 ? (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', flexDirection: 'column', gap: '2rem' }}>
            <div style={{ background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)', color: 'white', padding: '3rem 4rem', borderRadius: '16px', boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)', textAlign: 'center', maxWidth: '600px' }}>
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📂</div>
              <div style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '1rem' }}>등록된 요구사항이 없습니다</div>
              <div style={{ fontSize: '1rem', color: '#94a3b8', marginBottom: '2rem' }}>RFP 샘플 데이터를 업로드하여 시작하세요</div>
              <input ref={fileInputRef} type="file" accept=".json" onChange={handleFileUpload} style={{ display: 'none' }} />
              <button onClick={() => fileInputRef.current?.click()} disabled={uploading} style={{ padding: '1rem 2rem', background: uploading ? '#64748b' : '#3b82f6', color: 'white', border: 'none', borderRadius: '12px', cursor: 'pointer', fontSize: '1.1rem', fontWeight: 600 }}>
                {uploading ? '⚙️ 업로드 중...' : '📤 JSON 파일 업로드'}
              </button>
            </div>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.2rem', width: '100%' }}>
            {/* Batch Action Banner */}
            {requirements.length > 0 && (
              <div style={{
                background: 'linear-gradient(135deg, rgba(168, 85, 247, 0.15) 0%, rgba(126, 34, 206, 0.1) 100%)',
                border: '1px solid rgba(168, 85, 247, 0.3)',
                borderRadius: '16px',
                padding: '1.5rem 2rem',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                marginBottom: '0.8rem',
                boxShadow: '0 4px 15px rgba(0, 0, 0, 0.2)',
                animation: 'fadeIn 0.5s ease-out'
              }}>
                <div>
                  <div style={{ fontSize: '1.1rem', fontWeight: 700, color: '#c084fc', marginBottom: '0.25rem' }}>
                    ⚡ 과업 일괄 처리 ({requirements.length}개 요구사항)
                  </div>
                  <div style={{ fontSize: '0.9rem', color: '#94a3b8' }}>
                    등록된 모든 요구사항에 대해 AI 분석 및 비즈니스 과업 생성을 시작합니다.
                  </div>
                </div>
                <button
                  onClick={generateSequentialTasks}
                  disabled={batchGenerating}
                  style={{
                    padding: '0.8rem 1.5rem',
                    background: batchGenerating 
                      ? '#475569' 
                      : 'linear-gradient(135deg, #a855f7 0%, #7e22ce 100%)',
                    color: 'white',
                    border: 'none',
                    borderRadius: '12px',
                    cursor: batchGenerating ? 'not-allowed' : 'pointer',
                    fontWeight: 700,
                    fontSize: '0.95rem',
                    boxShadow: '0 4px 15px rgba(168, 85, 247, 0.4)',
                    transition: 'all 0.3s'
                  }}
                >
                  {batchGenerating ? '⏳ 분석 중...' : '전체 순차 생성 시작'}
                </button>
              </div>
            )}
            {(requirements || []).map((req, index) => {
              const relatedTasks = (taskCards || []).filter(t => 
                t.parentRequirementId?.trim() === req.requirementId?.trim()
              );

              return (
                <RequirementCard
                  key={index}
                  req={req}
                  index={index}
                  relatedTasks={relatedTasks}
                  isGenerating={generatingTasks.has(index)}
                  generationStatus={generationStatus.get(index)}
                  isTaskViewCollapsed={collapsedTaskViews.has(req.requirementId)}
                  onToggleTaskView={() => toggleTaskView(req.requirementId)}
                  onGenerateTasks={() => generateTaskCards(req, index)}
                  onStartEditing={() => startEditing(index, req)}
                  isEditing={editingIndex === index}
                  editingContent={editingContent}
                  setEditingContent={(updates: any) => setEditingContent(prev => ({ ...prev, ...updates }))}
                  onSaveEditing={() => saveEditing(req, index)}
                  onCancelEditing={() => setEditingIndex(null)}
                  hasAnyTaskCards={relatedTasks.length > 0}
                  leftWidth={leftWidth}
                  onResizeStart={() => setIsResizing(true)}
                  isResizing={isResizing}
                />
              );
            })}
          </div>
        )}
      </main>

      <BatchProgress current={batchProgress.current} total={batchProgress.total} />

      <style>{`
        @keyframes shimmer { 0% { transform: translateX(-100%); } 100% { transform: translateX(100%); } }
        @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
        @keyframes slideUp { from { transform: translateY(100px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
      `}</style>
    </div>
  );
}

export default App;
