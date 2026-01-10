import './App.css'
import rfpData from './sample/rfp_sample.json'
import { useState } from 'react'
import * as React from 'react'

interface Requirement {
  rfpId: string
  requirementId: string
  name: string
  definition: string
  requestContent: string
  deadline: string
  implementationOpinion: string
  businessDevelopment: string
  pobaOpinion: string
  techInnovationOpinion: string
}

interface TaskCard {
  id: string
  parentRequirementId: string
  parentIndex: number
  summary: string
  majorCategoryId: string
  majorCategory: string
  detailFunctionId: string
  detailFunction: string
  subFunction: string
}

function App() {
  const requirements: Requirement[] = rfpData
  const [expandedCards, setExpandedCards] = useState<Set<number>>(new Set())
  const [cardWidth, setCardWidth] = useState(1000)
  const [leftColumnWidth, setLeftColumnWidth] = useState(250)
  const [taskCards, setTaskCards] = useState<TaskCard[]>([])
  const [collapsedLeftColumns, setCollapsedLeftColumns] = useState<Set<number>>(new Set())
  const [collapsedTaskViews, setCollapsedTaskViews] = useState<Set<number>>(new Set())
  const [isResizing, setIsResizing] = useState(false)
  const [globalRfpWidth, setGlobalRfpWidth] = useState(window.innerWidth * 0.5)
  const [autoCollapsedLeft, setAutoCollapsedLeft] = useState(false)

  const toggleExpand = (index: number) => {
    setExpandedCards(prev => {
      const next = new Set(prev)
      if (next.has(index)) {
        next.delete(index)
      } else {
        next.add(index)
      }
      return next
    })
  }

  const expandAll = () => {
    setExpandedCards(new Set(requirements.map((_, index) => index)))
  }

  const collapseAll = () => {
    setExpandedCards(new Set())
  }

  const toggleLeftColumn = (index: number) => {
    // 자동 접힘 상태일 때는 자동 접힘을 해제
    if (autoCollapsedLeft) {
      setAutoCollapsedLeft(false)
      return
    }

    setCollapsedLeftColumns(prev => {
      const next = new Set(prev)
      if (next.has(index)) {
        next.delete(index)
      } else {
        next.add(index)
      }
      return next
    })
  }

  const toggleTaskView = (index: number) => {
    setCollapsedTaskViews(prev => {
      const next = new Set(prev)
      if (next.has(index)) {
        next.delete(index)
      } else {
        next.add(index)
      }
      return next
    })
  }

  const handleMouseDown = () => {
    setIsResizing(true)
  }

  const handleMouseMove = (e: MouseEvent) => {
    if (!isResizing) return

    // 첫 번째 row를 찾아서 기준으로 사용
    const firstRow = document.querySelector('[id^="row-"]')
    if (!firstRow) return

    const rowRect = firstRow.getBoundingClientRect()
    const newWidth = e.clientX - rowRect.left

    const maxWidth = rowRect.width - 400 - 8 // 8px = resizer width
    if (newWidth >= 400 && newWidth <= maxWidth) {
      setGlobalRfpWidth(newWidth)

      // 900px 이하일 때 자동으로 왼쪽 컬럼 접기
      if (newWidth <= 900) {
        setAutoCollapsedLeft(true)
      } else {
        setAutoCollapsedLeft(false)
      }
    }
  }

  const handleMouseUp = () => {
    setIsResizing(false)
  }

  React.useEffect(() => {
    if (isResizing) {
      window.addEventListener('mousemove', handleMouseMove as any)
      window.addEventListener('mouseup', handleMouseUp)
      return () => {
        window.removeEventListener('mousemove', handleMouseMove as any)
        window.removeEventListener('mouseup', handleMouseUp)
      }
    }
  }, [isResizing])

  const generateTaskCards = (req: Requirement, index: number) => {
    // TODO: AI를 사용하여 과업 내용을 분석하고 여러 개의 태스크 카드 생성
    // 임시로 샘플 데이터 생성
    const newTasks: TaskCard[] = [
      {
        id: `${req.requirementId}-TASK-001`,
        parentRequirementId: req.requirementId,
        parentIndex: index,
        summary: `${req.name} - 기능 구현`,
        majorCategoryId: 'CAT-001',
        majorCategory: 'AI 모델 개발',
        detailFunctionId: 'FUNC-001',
        detailFunction: 'LLM 모델 구축',
        subFunction: '사전학습 모델 준비'
      },
      {
        id: `${req.requirementId}-TASK-002`,
        parentRequirementId: req.requirementId,
        parentIndex: index,
        summary: `${req.name} - 테스트`,
        majorCategoryId: 'CAT-002',
        majorCategory: '품질 검증',
        detailFunctionId: 'FUNC-002',
        detailFunction: '성능 테스트',
        subFunction: '모델 정확도 측정'
      }
    ]

    setTaskCards(prev => [...prev, ...newTasks])
    setCollapsedTaskViews(prev => {
      const next = new Set(prev)
      next.delete(index)
      return next
    })
    alert(`${newTasks.length}개의 과업 카드가 생성되었습니다.`)
  }

  return (
    <div style={{ width: '100vw', height: '100vh', display: 'flex', flexDirection: 'column', margin: 0, padding: 0 }}>
      <header style={{
        backgroundColor: '#1a1a2e',
        color: 'white',
        padding: '0.75rem 1.5rem',
        borderBottom: '2px solid #0f3460',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <h1 style={{ margin: 0, fontSize: '1.3rem', fontWeight: 600 }}>RFP 요구사항 관리</h1>
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span style={{ fontSize: '0.85rem', color: 'white' }}>카드 너비:</span>
            <input
              type="range"
              min="800"
              max="2000"
              step="50"
              value={cardWidth}
              onChange={(e) => setCardWidth(Number(e.target.value))}
              style={{
                width: '150px',
                cursor: 'pointer'
              }}
            />
            <span style={{ fontSize: '0.85rem', color: 'white', minWidth: '60px' }}>{cardWidth}px</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span style={{ fontSize: '0.85rem', color: 'white' }}>왼쪽 너비:</span>
            <input
              type="range"
              min="200"
              max="500"
              step="10"
              value={leftColumnWidth}
              onChange={(e) => setLeftColumnWidth(Number(e.target.value))}
              style={{
                width: '150px',
                cursor: 'pointer'
              }}
            />
            <span style={{ fontSize: '0.85rem', color: 'white', minWidth: '60px' }}>{leftColumnWidth}px</span>
          </div>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button
              onClick={expandAll}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: '#0f3460',
                color: 'white',
                border: '1px solid #3a5f8f',
                borderRadius: '6px',
                cursor: 'pointer',
                fontSize: '0.85rem',
                fontWeight: 500,
                transition: 'all 0.2s'
              }}
            >
              전체 펼치기
            </button>
            <button
              onClick={collapseAll}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: '#0f3460',
                color: 'white',
                border: '1px solid #3a5f8f',
                borderRadius: '6px',
                cursor: 'pointer',
                fontSize: '0.85rem',
                fontWeight: 500,
                transition: 'all 0.2s'
              }}
            >
              전체 닫기
            </button>
          </div>
        </div>
      </header>

      <main style={{
        flex: 1,
        padding: '1.5rem',
        overflowY: 'auto',
        backgroundColor: '#f5f5f5'
      }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.2rem' }}>
          {requirements.map((req, index) => {
            const relatedTasks = taskCards.filter(t => t.parentIndex === index)
            const hasAnyTaskCards = taskCards.length > 0

            return (
              <div
                key={index}
                id={`row-${index}`}
                style={{
                  display: 'flex',
                  gap: 0,
                  alignItems: 'stretch',
                  position: 'relative'
                }}
              >
                <div
                  id={`req-card-${index}`}
                  style={{
                    border: '1px solid #ddd',
                    borderRadius: '8px',
                    padding: '1.5rem',
                    backgroundColor: 'white',
                    boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
                    display: 'grid',
                    gridTemplateColumns: (collapsedLeftColumns.has(index) || autoCollapsedLeft)
                      ? `40px 1fr auto`
                      : `${leftColumnWidth}px 1fr auto`,
                    gap: '2rem',
                    transition: isResizing ? 'none' : 'grid-template-columns 0.3s, width 0.3s',
                    width: hasAnyTaskCards ? `${globalRfpWidth}px` : `${cardWidth}px`,
                    minWidth: hasAnyTaskCards ? '400px' : undefined,
                    flexShrink: 0
                  }}
                >
              {(collapsedLeftColumns.has(index) || autoCollapsedLeft) ? (
                <div style={{ display: 'flex', alignItems: 'flex-start', paddingTop: '0.5rem' }}>
                  <button
                    onClick={() => toggleLeftColumn(index)}
                    style={{
                      padding: '0.5rem',
                      backgroundColor: '#f3f4f6',
                      border: '1px solid #d1d5db',
                      borderRadius: '6px',
                      cursor: 'pointer',
                      fontSize: '1.2rem',
                      color: '#374151',
                      transition: 'all 0.2s',
                      width: '40px',
                      height: '40px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center'
                    }}
                  >
                    ▶
                  </button>
                </div>
              ) : (
              <div>
                <div style={{
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '0.5rem',
                  marginBottom: '1rem',
                  paddingBottom: '0.75rem',
                  borderBottom: '1px solid #e5e7eb'
                }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span style={{ fontSize: '0.85rem', color: '#6b7280', fontWeight: 600 }}>RFP ID</span>
                    <span style={{ fontSize: '0.95rem', color: '#1f2937', fontWeight: 500 }}>{req.rfpId}</span>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span style={{ fontSize: '0.85rem', color: '#6b7280', fontWeight: 600 }}>요구사항 ID</span>
                    <span style={{ fontSize: '0.95rem', color: '#1f2937', fontWeight: 500 }}>{req.requirementId}</span>
                  </div>
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.1rem', fontWeight: 600, color: '#111827', marginBottom: '0.25rem' }}>
                      {req.name}
                    </div>
                    <div style={{ fontSize: '0.9rem', color: '#6b7280' }}>
                      {req.definition}
                    </div>
                  </div>
                  <button
                    onClick={() => toggleLeftColumn(index)}
                    style={{
                      padding: '0.4rem 0.6rem',
                      backgroundColor: '#f3f4f6',
                      border: '1px solid #d1d5db',
                      borderRadius: '6px',
                      cursor: 'pointer',
                      fontSize: '1rem',
                      color: '#374151',
                      transition: 'all 0.2s',
                      marginLeft: '1rem'
                    }}
                  >
                    ◀
                  </button>
                </div>
              </div>
              )}

              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <div>
                  <div style={{
                    fontSize: '0.85rem',
                    fontWeight: 600,
                    color: '#0f3460',
                    marginBottom: '0.5rem',
                    textTransform: 'uppercase',
                    letterSpacing: '0.5px'
                  }}>
                    제안요청내용
                  </div>
                  <div style={{
                    whiteSpace: 'pre-wrap',
                    lineHeight: '1.7',
                    color: '#374151',
                    backgroundColor: '#f9fafb',
                    padding: '1rem',
                    borderRadius: '6px',
                    borderLeft: '3px solid #0f3460',
                    fontSize: '0.95rem'
                  }}>
                    {req.requestContent}
                  </div>
                </div>

                <div>
                  <button
                    onClick={() => toggleExpand(index)}
                    style={{
                      width: '100%',
                      padding: '0.75rem 1rem',
                      backgroundColor: '#f3f4f6',
                      border: '1px solid #d1d5db',
                      borderRadius: '6px',
                      cursor: 'pointer',
                      fontSize: '0.9rem',
                      fontWeight: 600,
                      color: '#374151',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      transition: 'all 0.2s'
                    }}
                  >
                    <span>상세 정보 {expandedCards.has(index) ? '숨기기' : '보기'}</span>
                    <span style={{ fontSize: '1.2rem' }}>{expandedCards.has(index) ? '▲' : '▼'}</span>
                  </button>

                  {expandedCards.has(index) && (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '1rem' }}>
                      {req.deadline && (
                        <div>
                          <div style={{
                            fontSize: '0.85rem',
                            fontWeight: 600,
                            color: '#0f3460',
                            marginBottom: '0.5rem'
                          }}>
                            기능 제공 기한
                          </div>
                          <div style={{
                            fontSize: '0.95rem',
                            color: '#374151',
                            padding: '0.5rem 1rem',
                            backgroundColor: '#f9fafb',
                            borderRadius: '6px'
                          }}>
                            {req.deadline}
                          </div>
                        </div>
                      )}

                      {req.implementationOpinion && (
                        <div>
                          <div style={{
                            fontSize: '0.85rem',
                            fontWeight: 600,
                            color: '#0f3460',
                            marginBottom: '0.5rem'
                          }}>
                            이행 의견
                          </div>
                          <div style={{
                            whiteSpace: 'pre-wrap',
                            fontSize: '0.95rem',
                            color: '#374151',
                            padding: '0.75rem 1rem',
                            backgroundColor: '#f9fafb',
                            borderRadius: '6px',
                            lineHeight: '1.6'
                          }}>
                            {req.implementationOpinion}
                          </div>
                        </div>
                      )}

                      {req.pobaOpinion && (
                        <div>
                          <div style={{
                            fontSize: '0.85rem',
                            fontWeight: 600,
                            color: '#0f3460',
                            marginBottom: '0.5rem'
                          }}>
                            PO/BA 의견
                          </div>
                          <div style={{
                            whiteSpace: 'pre-wrap',
                            fontSize: '0.95rem',
                            color: '#374151',
                            padding: '0.75rem 1rem',
                            backgroundColor: '#f9fafb',
                            borderRadius: '6px',
                            lineHeight: '1.6'
                          }}>
                            {req.pobaOpinion}
                          </div>
                        </div>
                      )}

                      {req.techInnovationOpinion && (
                        <div>
                          <div style={{
                            fontSize: '0.85rem',
                            fontWeight: 600,
                            color: '#0f3460',
                            marginBottom: '0.5rem'
                          }}>
                            기술혁신 의견
                          </div>
                          <div style={{
                            whiteSpace: 'pre-wrap',
                            fontSize: '0.95rem',
                            color: '#374151',
                            padding: '0.75rem 1rem',
                            backgroundColor: '#f9fafb',
                            borderRadius: '6px',
                            lineHeight: '1.6'
                          }}>
                            {req.techInnovationOpinion}
                          </div>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              </div>

                  <div style={{ display: 'flex', alignItems: 'flex-start', paddingTop: '0.5rem' }}>
                    <button
                      id={`btn-${index}`}
                      onClick={() => generateTaskCards(req, index)}
                      style={{
                        padding: '0.75rem 1rem',
                        backgroundColor: '#0f3460',
                        color: 'white',
                        border: 'none',
                        borderRadius: '6px',
                        cursor: 'pointer',
                        fontSize: '0.9rem',
                        fontWeight: 600,
                        whiteSpace: 'nowrap',
                        transition: 'all 0.2s',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.backgroundColor = '#1a4d7a'
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.backgroundColor = '#0f3460'
                      }}
                    >
                      과업 생성
                    </button>
                  </div>
                </div>

                {hasAnyTaskCards && (
                  <>
                    {/* 리사이저 */}
                    <div
                      onMouseDown={handleMouseDown}
                      style={{
                        width: '8px',
                        cursor: 'col-resize',
                        backgroundColor: isResizing ? '#667eea' : '#e5e7eb',
                        transition: 'background-color 0.2s',
                        flexShrink: 0,
                        position: 'relative'
                      }}
                      onMouseEnter={(e) => {
                        if (!isResizing) {
                          e.currentTarget.style.backgroundColor = '#cbd5e1'
                        }
                      }}
                      onMouseLeave={(e) => {
                        if (!isResizing) {
                          e.currentTarget.style.backgroundColor = '#e5e7eb'
                        }
                      }}
                    >
                      <div style={{
                        position: 'absolute',
                        top: '50%',
                        left: '50%',
                        transform: 'translate(-50%, -50%)',
                        width: '2px',
                        height: '40px',
                        backgroundColor: 'rgba(255, 255, 255, 0.5)',
                        borderRadius: '1px'
                      }} />
                    </div>

                    {/* 과업 영역 */}
                    {relatedTasks.length > 0 && (
                    <div style={{
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '1rem',
                      flex: 1,
                      minWidth: '400px',
                      padding: '0 0 0 2rem'
                    }}>
                    <div
                      onClick={() => toggleTaskView(index)}
                      style={{
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                        color: 'white',
                        padding: '1rem 1.5rem',
                        borderRadius: '10px',
                        fontSize: '1rem',
                        fontWeight: 700,
                        boxShadow: '0 4px 12px rgba(102, 126, 234, 0.3)',
                        textAlign: 'center',
                        letterSpacing: '0.5px',
                        cursor: 'pointer',
                        transition: 'all 0.2s'
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.transform = 'scale(1.02)'
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.transform = 'scale(1)'
                      }}
                    >
                      {collapsedTaskViews.has(index)
                        ? `✓ 생성된 과업 ${relatedTasks.length}개 (클릭하여 열기)`
                        : `✓ 생성된 과업 ${relatedTasks.length}개 (클릭하여 닫기)`
                      }
                    </div>
                    {!collapsedTaskViews.has(index) && (
                    <>
                    {relatedTasks.map((task, taskIndex) => (
                      <div
                        key={task.id}
                        style={{
                          background: 'linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%)',
                          border: '2px solid #667eea',
                          borderRadius: '12px',
                          padding: '1.5rem',
                          boxShadow: '0 4px 16px rgba(102, 126, 234, 0.15)',
                          transition: 'all 0.3s',
                          position: 'relative',
                          overflow: 'hidden'
                        }}
                      >
                        <div style={{
                          position: 'absolute',
                          top: 0,
                          left: 0,
                          width: '100%',
                          height: '4px',
                          background: 'linear-gradient(90deg, #667eea 0%, #764ba2 100%)'
                        }} />

                        <div style={{
                          display: 'inline-block',
                          backgroundColor: '#667eea',
                          color: 'white',
                          padding: '0.25rem 0.75rem',
                          borderRadius: '20px',
                          fontSize: '0.75rem',
                          fontWeight: 700,
                          marginBottom: '1rem',
                          letterSpacing: '0.5px'
                        }}>
                          TASK #{taskIndex + 1}
                        </div>

                        <div style={{
                          marginBottom: '1rem',
                          paddingBottom: '1rem',
                          borderBottom: '1px dashed #e0e7ff'
                        }}>
                          <div style={{ fontSize: '0.8rem', color: '#8b92b0', marginBottom: '0.25rem', fontWeight: 600 }}>
                            과업 ID
                          </div>
                          <div style={{ fontWeight: 700, color: '#667eea', fontSize: '1.1rem' }}>
                            {task.id}
                          </div>
                        </div>

                        <div style={{
                          backgroundColor: '#f0f4ff',
                          borderRadius: '8px',
                          padding: '1rem',
                          marginBottom: '1rem',
                          display: 'grid',
                          gridTemplateColumns: '1fr 1fr',
                          gap: '1rem'
                        }}>
                          <div>
                            <div style={{ fontSize: '0.75rem', color: '#8b92b0', marginBottom: '0.25rem', fontWeight: 600, textTransform: 'uppercase' }}>
                              기능 대분류
                            </div>
                            <div style={{ fontSize: '1rem', fontWeight: 700, color: '#1a1a2e' }}>
                              {task.majorCategory}
                            </div>
                            <div style={{ fontSize: '0.75rem', color: '#667eea', marginTop: '0.25rem' }}>
                              [{task.majorCategoryId}]
                            </div>
                          </div>

                          <div>
                            <div style={{ fontSize: '0.75rem', color: '#8b92b0', marginBottom: '0.25rem', fontWeight: 600, textTransform: 'uppercase' }}>
                              상세 기능
                            </div>
                            <div style={{ fontSize: '1rem', fontWeight: 700, color: '#1a1a2e' }}>
                              {task.detailFunction}
                            </div>
                            <div style={{ fontSize: '0.75rem', color: '#667eea', marginTop: '0.25rem' }}>
                              [{task.detailFunctionId}]
                            </div>
                          </div>
                        </div>

                        <div style={{
                          marginBottom: '1rem',
                          backgroundColor: '#fff',
                          padding: '0.75rem 1rem',
                          borderRadius: '8px',
                          border: '1px solid #e0e7ff'
                        }}>
                          <div style={{ fontSize: '0.75rem', color: '#8b92b0', marginBottom: '0.5rem', fontWeight: 600, textTransform: 'uppercase' }}>
                            세부 기능
                          </div>
                          <div style={{ fontSize: '0.95rem', color: '#1a1a2e', fontWeight: 500, lineHeight: '1.5' }}>
                            {task.subFunction}
                          </div>
                        </div>

                        <div>
                          <div style={{
                            fontSize: '0.75rem',
                            color: '#8b92b0',
                            marginBottom: '0.75rem',
                            fontWeight: 600,
                            textTransform: 'uppercase',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem'
                          }}>
                            <span style={{
                              display: 'inline-block',
                              width: '3px',
                              height: '14px',
                              backgroundColor: '#667eea',
                              borderRadius: '2px'
                            }} />
                            과업 내용 요약
                          </div>
                          <div style={{
                            fontSize: '0.95rem',
                            color: '#1a1a2e',
                            padding: '1rem 1.25rem',
                            backgroundColor: '#fafbff',
                            borderRadius: '8px',
                            border: '2px solid #e0e7ff',
                            lineHeight: '1.7',
                            fontWeight: 500
                          }}>
                            {task.summary}
                          </div>
                        </div>
                      </div>
                    ))}
                    </>
                    )}
                    </div>
                    )}
                    {relatedTasks.length === 0 && (
                      <div style={{ flex: 1 }} />
                    )}
                  </>
                )}
              </div>
            )
          })}
        </div>
      </main>
    </div>
  )
}

export default App
