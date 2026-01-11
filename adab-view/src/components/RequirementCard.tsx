import React from 'react';
import type { TaskCard as TaskCardType, Requirement } from '../api';
import { TaskCard } from './TaskCard';

interface RequirementCardProps {
  req: Requirement;
  index: number;
  relatedTasks: TaskCardType[];
  isGenerating: boolean;
  generationStatus?: string;
  isTaskViewCollapsed: boolean;
  onToggleTaskView: () => void;
  onGenerateTasks: () => void;
  onStartEditing: () => void;
  isEditing: boolean;
  editingContent: Partial<Requirement>;
  setEditingContent: (updates: Partial<Requirement>) => void;
  onSaveEditing: () => void;
  onCancelEditing: () => void;
  hasAnyTaskCards: boolean;
  leftWidth: number;
  onResizeStart: () => void;
  isResizing: boolean;
}

export const RequirementCard: React.FC<RequirementCardProps> = ({
  req,
  index,
  relatedTasks,
  isGenerating,
  generationStatus,
  onGenerateTasks,
  onStartEditing,
  isTaskViewCollapsed,
  onToggleTaskView,
  isEditing,
  editingContent,
  setEditingContent,
  onSaveEditing,
  onCancelEditing,
  hasAnyTaskCards,
  leftWidth,
  onResizeStart,
  isResizing
}) => {
  // 과업이 있는 경우, 접힘 상태가 아니면 무조건 보이도록 보장
  const showTaskContent = hasAnyTaskCards && !isTaskViewCollapsed;

  return (
    <div
      id={`row-${index}`}
      style={{
        display: 'flex',
        gap: 0,
        alignItems: 'stretch',
        position: 'relative',
        justifyContent: (hasAnyTaskCards || isGenerating) ? 'flex-start' : 'center',
        width: '100%',
        minWidth: '100%',
        minHeight: '200px'
      }}
    >
      <div
        id={`req-card-${index}`}
        style={{
          border: '1px solid rgba(51, 65, 85, 0.5)',
          borderRadius: '16px',
          padding: '1.5rem',
          background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
          boxShadow: '0 8px 24px rgba(0,0,0,0.5), 0 0 0 1px rgba(148, 163, 184, 0.1)',
          display: 'grid',
          gridTemplateColumns: `1.5fr auto`,
          gap: '1.5rem',
          width: (hasAnyTaskCards || isGenerating) ? `${leftWidth}px` : '100%',
          maxWidth: (hasAnyTaskCards || isGenerating) ? `${leftWidth}px` : '1200px',
          transition: isResizing ? 'none' : 'height 0.4s ease',
          position: 'relative',
          overflow: 'hidden',
          flexShrink: 0
        }}
      >
        {/* Main Content Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', flex: 1 }}>
          {/* Top Info Grid - 2x2 Layout as requested */}
          <div style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            gap: '1rem',
            padding: '1.25rem',
            background: 'rgba(30, 41, 59, 0.4)',
            borderRadius: '12px',
            border: '1px solid rgba(148, 163, 184, 0.1)',
            marginBottom: '0.5rem'
          }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
              <span style={{ fontSize: '0.7rem', color: '#94a3b8', fontWeight: 600 }}>서비스명</span>
              <span style={{ fontSize: '0.95rem', color: '#f8fafc', fontWeight: 700 }}>{req.name}</span>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
              <span style={{ fontSize: '0.7rem', color: '#94a3b8', fontWeight: 600 }}>요구사항 정의</span>
              <span style={{ fontSize: '0.95rem', color: '#f8fafc', fontWeight: 700 }}>{req.definition}</span>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
              <span style={{ fontSize: '0.7rem', color: '#60a5fa', fontWeight: 600 }}>RFP ID</span>
              <span style={{ fontSize: '0.95rem', color: '#f8fafc', fontWeight: 700 }}>{req.rfpId}</span>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
              <span style={{ fontSize: '0.7rem', color: '#60a5fa', fontWeight: 600 }}>요구사항 ID</span>
              <span style={{ fontSize: '0.95rem', color: '#f8fafc', fontWeight: 700 }}>{req.requirementId}</span>
            </div>
          </div>

          <div style={{
            fontSize: '1rem',
            fontWeight: 800,
            color: '#c084fc',
            marginBottom: '0.5rem',
            textTransform: 'uppercase',
            letterSpacing: '1.5px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            textShadow: '0 0 10px rgba(168, 85, 247, 0.3)'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <span style={{
                display: 'inline-block',
                width: '24px',
                height: '3px',
                background: 'linear-gradient(90deg, #a855f7 0%, transparent 100%)',
                borderRadius: '2px'
              }} />
              제안요청내용
            </div>
            <div style={{ display: 'flex', gap: '0.6rem' }}>
              {isEditing ? (
                <>
                  <button
                    onClick={onSaveEditing}
                    style={{
                      padding: '0.6rem 1.25rem',
                      background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
                      color: 'white',
                      border: 'none',
                      borderRadius: '10px',
                      cursor: 'pointer',
                      fontSize: '0.85rem',
                      fontWeight: 700,
                      whiteSpace: 'nowrap',
                      boxShadow: '0 4px 12px rgba(16, 185, 129, 0.3)',
                      transition: 'all 0.2s'
                    }}
                  >
                    💾 저장
                  </button>
                  <button
                    onClick={onCancelEditing}
                    style={{
                      padding: '0.6rem 1.25rem',
                      background: 'linear-gradient(135deg, #64748b 0%, #475569 100%)',
                      color: 'white',
                      border: 'none',
                      borderRadius: '10px',
                      cursor: 'pointer',
                      fontSize: '0.85rem',
                      fontWeight: 700,
                      whiteSpace: 'nowrap',
                      transition: 'all 0.2s'
                    }}
                  >
                    ✕ 취소
                  </button>
                </>
              ) : (
                <>
                  <button
                    onClick={onStartEditing}
                    style={{
                      padding: '0.6rem 1.25rem',
                      background: 'rgba(139, 92, 246, 0.1)',
                      color: '#a78bfa',
                      border: '1px solid rgba(139, 92, 246, 0.3)',
                      borderRadius: '10px',
                      cursor: 'pointer',
                      fontSize: '0.85rem',
                      fontWeight: 700,
                      whiteSpace: 'nowrap',
                      transition: 'all 0.2s'
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.background = 'rgba(139, 92, 246, 0.2)';
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.background = 'rgba(139, 92, 246, 0.1)';
                    }}
                  >
                    ✏️ 수정
                  </button>
                  <button
                    onClick={onGenerateTasks}
                    disabled={isGenerating}
                    style={{
                      padding: '0.6rem 1.25rem',
                      background: isGenerating
                        ? 'rgba(100, 116, 139, 0.4)'
                        : 'linear-gradient(135deg, #a855f7 0%, #7e22ce 100%)',
                      color: 'white',
                      border: isGenerating ? '1px solid #475569' : 'none',
                      borderRadius: '10px',
                      cursor: isGenerating ? 'not-allowed' : 'pointer',
                      fontSize: '0.85rem',
                      fontWeight: 700,
                      whiteSpace: 'nowrap',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '0.6rem',
                      boxShadow: isGenerating ? 'none' : '0 4px 20px rgba(168, 85, 247, 0.4)',
                      transition: 'all 0.3s ease'
                    }}
                  >
                    {isGenerating ? (
                      <>
                        <span style={{ display: 'inline-block', animation: 'spin 1s linear infinite' }}>⚙️</span> AI 분석 중...
                      </>
                    ) : '✨ AI로 과업 생성'}
                  </button>
                </>
              )}
            </div>
          </div>

          {isEditing ? (
            <textarea
              value={editingContent.requestContent}
              onChange={(e) => setEditingContent({ requestContent: e.target.value })}
              style={{
                width: '100%',
                minHeight: '250px',
                whiteSpace: 'pre-wrap',
                lineHeight: '1.8',
                color: '#f8fafc',
                backgroundColor: 'rgba(15, 23, 42, 0.8)',
                padding: '1.5rem',
                borderRadius: '16px',
                border: '2px solid #a855f7',
                fontSize: '1.1rem',
                fontWeight: 500,
                fontFamily: 'inherit',
                resize: 'vertical',
                boxShadow: 'inset 0 2px 10px rgba(0, 0, 0, 0.5)'
              }}
            />
          ) : (
            <div style={{
              whiteSpace: 'pre-wrap',
              lineHeight: '1.7',
              color: '#f1f5f9',
              background: 'linear-gradient(180deg, rgba(30, 41, 59, 0.6) 0%, rgba(15, 23, 42, 0.4) 100%)',
              padding: '1.75rem',
              borderRadius: '16px',
              borderLeft: '5px solid #a855f7',
              fontSize: '1.15rem',
              fontWeight: 500,
              boxShadow: '0 4px 15px rgba(0, 0, 0, 0.2)',
              letterSpacing: '0.2px'
            }}>
              {req.requestContent}
            </div>
          )}

          {/* Opinions and Deadline - Always visible */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '1rem' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div>
                <div style={{ fontSize: '0.8rem', fontWeight: 600, color: '#94a3b8', marginBottom: '0.5rem' }}>구축 의견</div>
                {isEditing ? (
                  <textarea
                    value={editingContent.implementationOpinion}
                    onChange={(e) => setEditingContent({ implementationOpinion: e.target.value })}
                    style={{ width: '100%', padding: '0.75rem', background: '#1e293b', border: '1px solid #475569', borderRadius: '8px', color: 'white', minHeight: '80px' }}
                  />
                ) : (
                  <div style={{ fontSize: '0.9rem', color: '#cbd5e1', background: 'rgba(30, 41, 59, 0.3)', padding: '0.75rem', borderRadius: '8px', whiteSpace: 'pre-wrap' }}>{req.implementationOpinion || '-'}</div>
                )}
              </div>
              <div>
                <div style={{ fontSize: '0.8rem', fontWeight: 600, color: '#94a3b8', marginBottom: '0.5rem' }}>PO/BA 의견</div>
                {isEditing ? (
                  <textarea
                    value={editingContent.pobaOpinion}
                    onChange={(e) => setEditingContent({ pobaOpinion: e.target.value })}
                    style={{ width: '100%', padding: '0.75rem', background: '#1e293b', border: '1px solid #475569', borderRadius: '8px', color: 'white', minHeight: '80px' }}
                  />
                ) : (
                  <div style={{ fontSize: '0.9rem', color: '#cbd5e1', background: 'rgba(30, 41, 59, 0.3)', padding: '0.75rem', borderRadius: '8px', whiteSpace: 'pre-wrap' }}>{req.pobaOpinion || '-'}</div>
                )}
              </div>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div>
                <div style={{ fontSize: '0.8rem', fontWeight: 600, color: '#94a3b8', marginBottom: '0.5rem' }}>기술혁신 의견</div>
                {isEditing ? (
                  <textarea
                    value={editingContent.techInnovationOpinion}
                    onChange={(e) => setEditingContent({ techInnovationOpinion: e.target.value })}
                    style={{ width: '100%', padding: '0.75rem', background: '#1e293b', border: '1px solid #475569', borderRadius: '8px', color: 'white', minHeight: '80px' }}
                  />
                ) : (
                  <div style={{ fontSize: '0.9rem', color: '#cbd5e1', background: 'rgba(30, 41, 59, 0.3)', padding: '0.75rem', borderRadius: '8px', whiteSpace: 'pre-wrap' }}>{req.techInnovationOpinion || '-'}</div>
                )}
              </div>
              <div>
                <div style={{ fontSize: '0.8rem', fontWeight: 600, color: '#94a3b8', marginBottom: '0.5rem' }}>완료기한</div>
                {isEditing ? (
                  <input
                    type="text"
                    value={editingContent.deadline}
                    onChange={(e) => setEditingContent({ deadline: e.target.value })}
                    style={{ width: '100%', padding: '0.75rem', background: '#1e293b', border: '1px solid #475569', borderRadius: '8px', color: 'white' }}
                  />
                ) : (
                  <div style={{ fontSize: '0.9rem', color: '#cbd5e1', background: 'rgba(30, 41, 59, 0.3)', padding: '0.75rem', borderRadius: '8px' }}>{req.deadline || '-'}</div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Resize Handle Placeholder or Task Area */}
      {(hasAnyTaskCards || isGenerating) && (
        <>
          <div 
            onMouseDown={onResizeStart}
            style={{ 
              width: '1.5rem', 
              display: 'flex', 
              justifyContent: 'center', 
              alignItems: 'center',
              cursor: 'col-resize',
              userSelect: 'none',
              transition: 'all 0.2s'
            }}
            className="resize-handle"
          >
            <div style={{ width: '2px', height: '40px', background: 'rgba(148, 163, 184, 0.2)', borderRadius: '1px' }} />
          </div>

          <div style={{ flex: 1, paddingLeft: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem', minWidth: '400px' }}>
            {/* Task List - Always visible if entries exist or generating */}
            {relatedTasks.length > 0 && (
              <div
                onClick={onToggleTaskView}
                style={{
                  background: 'rgba(59, 130, 246, 0.1)',
                  color: '#60a5fa',
                  padding: '0.75rem 1.5rem',
                  borderRadius: '12px',
                  cursor: 'pointer',
                  textAlign: 'center',
                  fontWeight: 800,
                  border: '1px solid rgba(59, 130, 246, 0.3)',
                  transition: 'all 0.2s ease'
                } as React.CSSProperties}
                onMouseEnter={(e) => {
                  e.currentTarget.style.background = 'rgba(59, 130, 246, 0.15)';
                  e.currentTarget.style.borderColor = 'rgba(59, 130, 246, 0.5)';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.background = 'rgba(59, 130, 246, 0.1)';
                  e.currentTarget.style.borderColor = 'rgba(59, 130, 246, 0.3)';
                }}
              >
                ✨ {req.requirementId}의 과업 {relatedTasks.length}개 {isTaskViewCollapsed ? '▼' : '▲'}
              </div>
            )}

            {showTaskContent && relatedTasks.length > 0 && (
              <div style={{ 
                display: 'flex', 
                flexDirection: 'column', 
                gap: '1rem', 
                width: '100%'
              }}>
                {relatedTasks.map(task => (
                  <TaskCard key={task.id} task={task} />
                ))}
              </div>
            )}

            {/* Generation Status Indicator - Shown while generating */}
            {isGenerating && (
              <div style={{
                background: 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)',
                color: 'white',
                padding: '2rem',
                borderRadius: '16px',
                textAlign: 'center',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '1rem',
                border: '1px solid rgba(59, 130, 246, 0.3)',
                animation: 'glow 2s infinite ease-in-out',
                position: 'relative',
                overflow: 'hidden',
                minHeight: '150px'
              }}>
                <div style={{
                  position: 'absolute',
                  top: '50%',
                  left: '50%',
                  width: '300px',
                  height: '300px',
                  background: 'radial-gradient(circle, rgba(59, 130, 246, 0.15) 0%, transparent 70%)',
                  transform: 'translate(-50%, -50%)',
                  animation: 'pulse 2s infinite ease-in-out',
                  filter: 'blur(30px)',
                  pointerEvents: 'none'
                }} />
                
                <div style={{
                  position: 'absolute',
                  top: '-10%',
                  left: '-10%',
                  width: '120%',
                  height: '120%',
                  background: 'conic-gradient(from 0deg at 50% 50%, transparent 0%, rgba(59, 130, 246, 0.05) 15%, transparent 30%)',
                  animation: 'spin 8s linear infinite',
                  filter: 'blur(10px)',
                  pointerEvents: 'none'
                }} />
                
                <div style={{ 
                  fontSize: '2rem', 
                  filter: 'drop-shadow(0 0 10px rgba(59, 130, 246, 0.5))',
                  animation: 'pulse 1.5s infinite ease-in-out'
                }}>
                  ✨
                </div>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.3rem', zIndex: 1 }}>
                  <div style={{ fontSize: '1.1rem', fontWeight: 700, color: '#60a5fa' }}>
                    AI가 과업을 설계 중입니다...
                  </div>
                  <div style={{ fontSize: '0.85rem', color: '#94a3b8', fontWeight: 500 }}>
                    {generationStatus || '분석 중...'}
                  </div>
                </div>

                <div style={{ width: '60%', height: '3px', background: 'rgba(51, 65, 85, 0.5)', borderRadius: '2px', position: 'relative', overflow: 'hidden' }}>
                  <div style={{ position: 'absolute', top: 0, left: 0, height: '100%', width: '30%', background: 'linear-gradient(90deg, transparent, #3b82f6, transparent)', animation: 'shimmer 1.5s infinite linear' }} />
                </div>
              </div>
            )}

            {!isGenerating && relatedTasks.length === 0 && (
              <div style={{
                background: 'rgba(30, 41, 59, 0.2)',
                color: '#94a3b8',
                padding: '1.25rem 1.5rem',
                borderRadius: '12px',
                textAlign: 'center',
                border: '1px dashed #334155',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '0.75rem'
              }}>
                <span style={{ fontSize: '1rem' }}>✨</span>
                <span style={{ fontSize: '0.9rem', fontWeight: 600, color: '#64748b' }}>
                  {req.requirementId}의 과업 0개
                </span>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
};
