import './App.css'
import { useState, useEffect, useRef } from 'react'
import * as React from 'react'
import { getRequirements, getAllTasks, uploadRequirementsBatch, updateRequirement, generateTasksWithBackend, checkTasksExist, deleteTasksByRequirement, getCurrentModelName, type TaskCard } from './api'
import Settings from './Settings'

interface Requirement {
  rfpId: string
  requirementId: string
  name: string
  definition: string
  requestContent: string
  deadline: string
  implementationOpinion: string
  pobaOpinion: string
  techInnovationOpinion: string
  createdAt?: string
  updatedAt?: string
}

function App() {
  const [currentPage, setCurrentPage] = useState<'main' | 'settings'>('main')
  const [requirements, setRequirements] = useState<Requirement[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [currentModel, setCurrentModel] = useState<string>('Ollama gemma3:12b')
  // 모든 카드를 기본적으로 펼친 상태로 시작
  const [expandedCards, setExpandedCards] = useState<Set<number>>(() => {
    const allIndices = new Set<number>()
    for (let i = 0; i < 100; i++) { // 충분히 큰 숫자로 초기화
      allIndices.add(i)
    }
    return allIndices
  })
  const [cardWidth, setCardWidth] = useState(1000)
  const [leftColumnWidth, setLeftColumnWidth] = useState(250)
  const [taskCards, setTaskCards] = useState<TaskCard[]>([])
  const [collapsedLeftColumns, setCollapsedLeftColumns] = useState<Set<number>>(new Set())
  const [collapsedTaskViews, setCollapsedTaskViews] = useState<Set<number>>(new Set())
  const [isResizing, setIsResizing] = useState(false)
  const [globalRfpWidth, setGlobalRfpWidth] = useState(window.innerWidth * 0.5)
  const [autoCollapsedLeft, setAutoCollapsedLeft] = useState(false)
  const [generatingTasks, setGeneratingTasks] = useState<Set<number>>(new Set())
  const [generationStatus, setGenerationStatus] = useState<Map<number, string>>(new Map())
  const [uploading, setUploading] = useState(false)
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [editingIndex, setEditingIndex] = useState<number | null>(null)
  const [editingContent, setEditingContent] = useState<string>('')
  const [editingDeadline, setEditingDeadline] = useState<string>('')
  const [editingImplementationOpinion, setEditingImplementationOpinion] = useState<string>('')
  const [editingPobaOpinion, setEditingPobaOpinion] = useState<string>('')
  const [editingTechInnovationOpinion, setEditingTechInnovationOpinion] = useState<string>('')
  const [batchGenerating, setBatchGenerating] = useState(false)
  const [batchProgress, setBatchProgress] = useState<{current: number, total: number}>({current: 0, total: 0})

  // 백엔드에서 요구사항 및 과업 데이터 가져오기
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)

        // 요구사항, 과업 데이터, 현재 모델 정보를 병렬로 가져오기
        const [requirementsData, tasksData, modelName] = await Promise.all([
          getRequirements(),
          getAllTasks(),
          getCurrentModelName().catch((err) => {
            console.error('모델 정보 로드 실패:', err)
            return '설정된 모델 없음'
          })
        ])

        setCurrentModel(modelName)
        console.log('현재 사용 중인 모델:', modelName)

        setRequirements(requirementsData)

        // 과업 데이터가 있으면 변환하여 설정
        if (tasksData.success && tasksData.data && tasksData.data.length > 0) {
          const loadedTasks: TaskCard[] = tasksData.data.map((task: any) => {
            // parentIndex를 requirements 배열에서 찾기
            const parentIndex = requirementsData.findIndex(
              (req: Requirement) => req.requirementId === task.parentRequirementId
            )

            return {
              id: task.id,
              parentRequirementId: task.parentRequirementId,
              parentIndex: parentIndex >= 0 ? parentIndex : task.parentIndex || 0,
              summary: task.summary,
              majorCategoryId: task.majorCategoryId,
              majorCategory: task.majorCategory,
              detailFunctionId: task.detailFunctionId,
              detailFunction: task.detailFunction,
              subFunction: task.subFunction,
              generatedBy: task.generatedBy,
              createdAt: task.createdAt,
              updatedAt: task.updatedAt
            }
          })
          setTaskCards(loadedTasks)
        }

        setError(null)
      } catch (err) {
        console.error('데이터 로딩 실패:', err)
        setError('데이터를 불러오는데 실패했습니다.')
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  // 편집 시작
  const startEditing = (index: number, req: Requirement) => {
    setEditingIndex(index)
    setEditingContent(req.requestContent)
    setEditingDeadline(req.deadline || '')
    setEditingImplementationOpinion(req.implementationOpinion || '')
    setEditingPobaOpinion(req.pobaOpinion || '')
    setEditingTechInnovationOpinion(req.techInnovationOpinion || '')
  }

  // 편집 취소
  const cancelEditing = () => {
    setEditingIndex(null)
    setEditingContent('')
    setEditingDeadline('')
    setEditingImplementationOpinion('')
    setEditingPobaOpinion('')
    setEditingTechInnovationOpinion('')
  }

  // 저장
  const saveEditing = async (req: Requirement, index: number) => {
    try {
      const updatedReq = {
        ...req,
        requestContent: editingContent,
        deadline: editingDeadline,
        implementationOpinion: editingImplementationOpinion,
        pobaOpinion: editingPobaOpinion,
        techInnovationOpinion: editingTechInnovationOpinion
      }

      await updateRequirement(req.requirementId, updatedReq)

      // 로컬 상태 업데이트
      setRequirements(prev =>
        prev.map((r, i) => i === index ? {
          ...r,
          requestContent: editingContent,
          deadline: editingDeadline,
          implementationOpinion: editingImplementationOpinion,
          pobaOpinion: editingPobaOpinion,
          techInnovationOpinion: editingTechInnovationOpinion
        } : r)
      )

      setEditingIndex(null)
      setEditingContent('')
      setEditingDeadline('')
      setEditingImplementationOpinion('')
      setEditingPobaOpinion('')
      setEditingTechInnovationOpinion('')

      alert('✅ 수정이 완료되었습니다.')
    } catch (err: any) {
      console.error('수정 실패:', err)
      alert(`❌ 수정 실패: ${err.response?.data?.message || err.message}`)
    }
  }

  // 전체 과업 순차 생성
  const generateAllTasks = async () => {
    if (batchGenerating) return

    // 기존 과업 개수 확인
    const existingTaskCount = taskCards.length

    const confirmMessage = existingTaskCount > 0
      ? `⚠️ 전체 과업 생성 안내\n\n총 ${requirements.length}개의 요구사항에 대해 순차적으로 과업을 생성합니다.\n\n【중요】기존에 생성된 ${existingTaskCount}개의 과업이 모두 삭제되고 새로 생성됩니다.\n\n예상 소요 시간: 약 ${Math.ceil(requirements.length * 0.5)}분\n\n계속하시겠습니까?`
      : `총 ${requirements.length}개의 요구사항에 대해 순차적으로 과업을 생성합니다.\n\n예상 소요 시간: 약 ${Math.ceil(requirements.length * 0.5)}분\n\n계속하시겠습니까?`

    if (!confirm(confirmMessage)) {
      return
    }

    setBatchGenerating(true)
    setBatchProgress({current: 0, total: requirements.length})

    for (let i = 0; i < requirements.length; i++) {
      const req = requirements[i]

      try {
        setBatchProgress({current: i + 1, total: requirements.length})

        // 이미 과업이 있는지 확인
        const exists = await checkTasksExist(req.requirementId)

        if (exists.exists && exists.count > 0) {
          // 이미 과업이 있으면 삭제
          await deleteTasksByRequirement(req.requirementId)

          // 해당 요구사항의 과업 카드 제거
          setTaskCards(prev => prev.filter(t => t.parentRequirementId !== req.requirementId))
        }

        // 과업 생성
        await new Promise<void>((resolve, reject) => {
          generateTasksWithBackend(
            { ...req, index: i },
            (message) => {
              // 상태 업데이트
              setGenerationStatus(prev => new Map(prev).set(i, message))
            },
            (task) => {
              // 과업 추가
              setTaskCards(prev => [...prev, task])
            },
            () => {
              // 완료
              setGenerationStatus(prev => {
                const next = new Map(prev)
                next.delete(i)
                return next
              })

              // 과업이 생성된 위치로 스크롤
              setTimeout(() => {
                const rowElement = document.getElementById(`row-${i}`)
                if (rowElement) {
                  rowElement.scrollIntoView({ behavior: 'smooth', block: 'start' })
                }
              }, 100)

              resolve()
            },
            (error) => {
              console.error(`요구사항 ${req.requirementId} 과업 생성 실패:`, error)
              reject(error)
            }
          )
        })

        // 다음 요구사항 처리 전 1초 대기
        if (i < requirements.length - 1) {
          await new Promise(resolve => setTimeout(resolve, 1000))
        }

      } catch (error) {
        console.error(`요구사항 ${req.requirementId} 처리 중 오류:`, error)
        // 오류가 발생해도 계속 진행
      }
    }

    setBatchGenerating(false)
    setBatchProgress({current: 0, total: 0})
    alert('✅ 전체 과업 생성이 완료되었습니다!')
  }

  // 파일 업로드 핸들러
  const handleFileUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return

    try {
      setUploading(true)
      const result = await uploadRequirementsBatch(file)
      alert(`✅ ${result.message || '업로드 성공!'}\n${result.count}건의 요구사항이 저장되었습니다.`)

      // 데이터 새로고침
      const data = await getRequirements()
      setRequirements(data)
      setError(null)
    } catch (err: any) {
      console.error('파일 업로드 실패:', err)
      alert(`❌ 업로드 실패: ${err.response?.data?.message || err.message}`)
    } finally {
      setUploading(false)
      // 파일 입력 초기화
      if (fileInputRef.current) {
        fileInputRef.current.value = ''
      }
    }
  }

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

  const generateTaskCards = async (req: Requirement, index: number) => {
    // 이미 생성 중이면 중단
    if (generatingTasks.has(index)) {
      return
    }

    try {
      // 기존 과업 존재 여부 확인
      const existsResult = await checkTasksExist(req.requirementId)

      if (existsResult.exists && existsResult.count > 0) {
        const confirmed = window.confirm(
          `해당 요구사항(${req.requirementId})에 이미 ${existsResult.count}개의 과업이 존재합니다.\n` +
          `기존 과업을 모두 삭제하고 새로 생성하시겠습니까?\n\n` +
          `- 확인: 기존 과업 삭제 후 새로 생성\n` +
          `- 취소: 생성 취소`
        )

        if (!confirmed) {
          // 생성 취소
          return
        }

        // 기존 과업 삭제
        const deleteResult = await deleteTasksByRequirement(req.requirementId)
        console.log(`${deleteResult.deletedCount}개의 과업이 삭제되었습니다.`)

        // 화면에서도 제거
        setTaskCards(prev => prev.filter(t => t.parentRequirementId !== req.requirementId))
      }
    } catch (error) {
      console.error('과업 확인/삭제 오류:', error)
    }

    // 생성 시작
    setGeneratingTasks(prev => new Set(prev).add(index))
    setGenerationStatus(prev => new Map(prev).set(index, 'LLM 분석을 시작합니다...'))
    setCollapsedTaskViews(prev => {
      const next = new Set(prev)
      next.delete(index)
      return next
    })

    // 백엔드 API 호출
    await generateTasksWithBackend(
      { ...req, index },
      // onStatus
      (message: string) => {
        setGenerationStatus(prev => new Map(prev).set(index, message))
      },
      // onTask
      (task: TaskCard) => {
        setTaskCards(prev => [...prev, task])
      },
      // onComplete
      () => {
        setGeneratingTasks(prev => {
          const next = new Set(prev)
          next.delete(index)
          return next
        })
        setGenerationStatus(prev => {
          const next = new Map(prev)
          next.delete(index)
          return next
        })

        // 과업이 생성된 위치로 스크롤
        setTimeout(() => {
          const rowElement = document.getElementById(`row-${index}`)
          if (rowElement) {
            rowElement.scrollIntoView({ behavior: 'smooth', block: 'start' })
          }
        }, 100)
      },
      // onError
      (error: Error) => {
        console.error('과업 생성 실패:', error)
        setGenerationStatus(prev => new Map(prev).set(index, `❌ 오류: ${error.message}`))
        setTimeout(() => {
          setGeneratingTasks(prev => {
            const next = new Set(prev)
            next.delete(index)
            return next
          })
          setGenerationStatus(prev => {
            const next = new Map(prev)
            next.delete(index)
            return next
          })
        }, 3000)
      }
    )
  }

  // 로딩 상태
  if (loading) {
    return (
      <div style={{ width: '100vw', height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: '#1e293b' }}>
        <div style={{
          background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
          color: 'white',
          padding: '3rem 4rem',
          borderRadius: '16px',
          fontSize: '1.2rem',
          fontWeight: 600,
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)',
          textAlign: 'center'
        }}>
          <div style={{ marginBottom: '1rem', fontSize: '2rem' }}>⚙️</div>
          <div>요구사항을 불러오는 중...</div>
        </div>
      </div>
    )
  }

  // 에러 상태
  if (error) {
    return (
      <div style={{ width: '100vw', height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: '#1e293b' }}>
        <div style={{
          background: 'linear-gradient(135deg, #7f1d1d 0%, #991b1b 100%)',
          color: 'white',
          padding: '3rem 4rem',
          borderRadius: '16px',
          fontSize: '1.2rem',
          fontWeight: 600,
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)',
          textAlign: 'center'
        }}>
          <div style={{ marginBottom: '1rem', fontSize: '2rem' }}>❌</div>
          <div>{error}</div>
          <button
            onClick={() => window.location.reload()}
            style={{
              marginTop: '1.5rem',
              padding: '0.75rem 1.5rem',
              background: 'white',
              color: '#991b1b',
              border: 'none',
              borderRadius: '8px',
              cursor: 'pointer',
              fontSize: '1rem',
              fontWeight: 600
            }}
          >
            다시 시도
          </button>
        </div>
      </div>
    )
  }

  // 페이지 라우팅
  if (currentPage === 'settings') {
    return <Settings onBack={async () => {
      setCurrentPage('main')
      // 모델 정보 다시 가져오기
      try {
        const modelName = await getCurrentModelName()
        setCurrentModel(modelName)
      } catch (err) {
        console.error('모델 정보 로드 실패:', err)
      }
    }} />
  }

  return (
    <div style={{ width: '100vw', height: '100vh', display: 'flex', flexDirection: 'column', margin: 0, padding: 0 }}>
      <header style={{
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
        color: 'white',
        padding: '1.25rem 2rem',
        borderBottom: '1px solid rgba(148, 163, 184, 0.2)',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        boxShadow: '0 4px 20px rgba(0,0,0,0.4)',
        backdropFilter: 'blur(10px)'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <h1 style={{
            margin: 0,
            fontSize: '1.5rem',
            fontWeight: 700,
            background: 'linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            backgroundClip: 'text',
            letterSpacing: '-0.5px'
          }}>RFP 요구사항 관리</h1>
          <div style={{
            padding: '0.4rem 0.75rem',
            background: 'rgba(139, 92, 246, 0.15)',
            border: '1px solid rgba(139, 92, 246, 0.3)',
            borderRadius: '6px',
            fontSize: '0.75rem',
            color: '#a78bfa',
            fontWeight: 600,
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem'
          }}>
            <span>🤖</span>
            <span>{currentModel}</span>
          </div>
        </div>
        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
          {batchGenerating && (
            <div style={{
              padding: '0.5rem 1rem',
              backgroundColor: 'rgba(59, 130, 246, 0.2)',
              border: '1px solid #3b82f6',
              borderRadius: '6px',
              fontSize: '0.85rem',
              fontWeight: 500,
              color: '#60a5fa',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem'
            }}>
              <span style={{
                animation: 'spin 1s linear infinite',
                display: 'inline-block'
              }}>⚙️</span>
              {batchProgress.current}/{batchProgress.total} 생성 중...
            </div>
          )}
          <button
            onClick={generateAllTasks}
            disabled={batchGenerating || requirements.length === 0}
            style={{
              padding: '0.5rem 1rem',
              background: batchGenerating
                ? 'linear-gradient(135deg, #64748b 0%, #475569 100%)'
                : 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              cursor: batchGenerating || requirements.length === 0 ? 'not-allowed' : 'pointer',
              fontSize: '0.85rem',
              fontWeight: 600,
              transition: 'all 0.2s',
              opacity: batchGenerating || requirements.length === 0 ? 0.6 : 1,
              boxShadow: '0 2px 8px rgba(245, 158, 11, 0.3)'
            }}
            onMouseEnter={(e) => {
              if (!batchGenerating && requirements.length > 0) {
                e.currentTarget.style.transform = 'translateY(-2px)'
                e.currentTarget.style.boxShadow = '0 4px 12px rgba(245, 158, 11, 0.4)'
              }
            }}
            onMouseLeave={(e) => {
              if (!batchGenerating) {
                e.currentTarget.style.transform = 'translateY(0)'
                e.currentTarget.style.boxShadow = '0 2px 8px rgba(245, 158, 11, 0.3)'
              }
            }}
          >
            ✨ AI로 전체 과업 생성
          </button>
          <button
              onClick={() => setCurrentPage('settings')}
              style={{
                padding: '0.5rem 1rem',
                background: 'linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                cursor: 'pointer',
                fontSize: '0.85rem',
                fontWeight: 600,
                transition: 'all 0.2s',
                boxShadow: '0 2px 8px rgba(139, 92, 246, 0.3)'
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'translateY(-2px)'
                e.currentTarget.style.boxShadow = '0 4px 12px rgba(139, 92, 246, 0.4)'
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0)'
                e.currentTarget.style.boxShadow = '0 2px 8px rgba(139, 92, 246, 0.3)'
              }}
            >
              ⚙️ 모델 설정
            </button>
        </div>
      </header>

      <main style={{
        flex: 1,
        padding: '1.5rem',
        overflowY: 'auto',
        backgroundColor: '#1e293b'
      }}>
        {requirements.length === 0 ? (
          <div style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            height: '100%',
            flexDirection: 'column',
            gap: '2rem'
          }}>
            <div style={{
              background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
              color: 'white',
              padding: '3rem 4rem',
              borderRadius: '16px',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)',
              textAlign: 'center',
              maxWidth: '600px'
            }}>
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📂</div>
              <div style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '1rem' }}>
                등록된 요구사항이 없습니다
              </div>
              <div style={{ fontSize: '1rem', color: '#94a3b8', marginBottom: '2rem' }}>
                RFP 샘플 데이터를 업로드하여 시작하세요
              </div>

              <input
                ref={fileInputRef}
                type="file"
                accept=".json"
                onChange={handleFileUpload}
                style={{ display: 'none' }}
              />

              <button
                onClick={() => fileInputRef.current?.click()}
                disabled={uploading}
                style={{
                  padding: '1rem 2rem',
                  background: uploading
                    ? 'linear-gradient(135deg, #64748b 0%, #475569 100%)'
                    : 'linear-gradient(135deg, #3b82f6 0%, #1e40af 100%)',
                  color: 'white',
                  border: 'none',
                  borderRadius: '12px',
                  cursor: uploading ? 'not-allowed' : 'pointer',
                  fontSize: '1.1rem',
                  fontWeight: 600,
                  boxShadow: '0 4px 12px rgba(59, 130, 246, 0.4)',
                  transition: 'all 0.3s',
                  opacity: uploading ? 0.7 : 1
                }}
                onMouseEnter={(e) => {
                  if (!uploading) {
                    e.currentTarget.style.transform = 'translateY(-2px)'
                    e.currentTarget.style.boxShadow = '0 6px 16px rgba(59, 130, 246, 0.5)'
                  }
                }}
                onMouseLeave={(e) => {
                  if (!uploading) {
                    e.currentTarget.style.transform = 'translateY(0)'
                    e.currentTarget.style.boxShadow = '0 4px 12px rgba(59, 130, 246, 0.4)'
                  }
                }}
              >
                {uploading ? '⚙️ 업로드 중...' : '📤 JSON 파일 업로드'}
              </button>

              <div style={{
                marginTop: '2rem',
                fontSize: '0.85rem',
                color: '#64748b',
                lineHeight: '1.6'
              }}>
                <div>💡 rfp_sample.json 형식의 파일을 선택하세요</div>
                <div>일괄 업로드 API: POST /api/requirements/batch</div>
              </div>
            </div>
          </div>
        ) : (
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
                  position: 'relative',
                  justifyContent: hasAnyTaskCards ? 'flex-start' : 'center'
                }}
              >
                <div
                  id={`req-card-${index}`}
                  style={{
                    border: '1px solid rgba(51, 65, 85, 0.5)',
                    borderRadius: '16px',
                    padding: '2rem',
                    background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
                    boxShadow: '0 8px 24px rgba(0,0,0,0.5), 0 0 0 1px rgba(148, 163, 184, 0.1)',
                    display: 'grid',
                    gridTemplateColumns: (collapsedLeftColumns.has(index) || autoCollapsedLeft)
                      ? `40px 1fr auto`
                      : `${leftColumnWidth}px 1fr auto`,
                    gap: '2rem',
                    transition: isResizing ? 'none' : 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                    width: hasAnyTaskCards ? `${globalRfpWidth}px` : `${cardWidth}px`,
                    minWidth: hasAnyTaskCards ? '400px' : undefined,
                    flexShrink: 0
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.boxShadow = '0 12px 32px rgba(0,0,0,0.6), 0 0 0 1px rgba(96, 165, 250, 0.2)'
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.boxShadow = '0 8px 24px rgba(0,0,0,0.5), 0 0 0 1px rgba(148, 163, 184, 0.1)'
                  }}
                >
              {(collapsedLeftColumns.has(index) || autoCollapsedLeft) ? (
                <div style={{ display: 'flex', alignItems: 'flex-start', paddingTop: '0.5rem' }}>
                  <button
                    onClick={() => toggleLeftColumn(index)}
                    style={{
                      padding: '0.5rem',
                      backgroundColor: '#1e293b',
                      border: '1px solid #475569',
                      borderRadius: '8px',
                      cursor: 'pointer',
                      fontSize: '1.2rem',
                      color: '#94a3b8',
                      transition: 'all 0.2s',
                      width: '40px',
                      height: '40px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center'
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.backgroundColor = '#334155'
                      e.currentTarget.style.color = '#e2e8f0'
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.backgroundColor = '#1e293b'
                      e.currentTarget.style.color = '#94a3b8'
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
                  borderBottom: '1px solid #334155'
                }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span style={{ fontSize: '0.85rem', color: '#94a3b8', fontWeight: 600 }}>RFP ID</span>
                    <span style={{ fontSize: '0.95rem', color: '#cbd5e1', fontWeight: 500 }}>{req.rfpId}</span>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span style={{ fontSize: '0.85rem', color: '#94a3b8', fontWeight: 600 }}>요구사항 ID</span>
                    <span style={{ fontSize: '0.95rem', color: '#cbd5e1', fontWeight: 500 }}>{req.requirementId}</span>
                  </div>
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.1rem', fontWeight: 600, color: '#e2e8f0', marginBottom: '0.25rem' }}>
                      {req.name}
                    </div>
                    <div style={{ fontSize: '0.9rem', color: '#94a3b8' }}>
                      {req.definition}
                    </div>
                  </div>
                  <button
                    onClick={() => toggleLeftColumn(index)}
                    style={{
                      padding: '0.4rem 0.6rem',
                      backgroundColor: '#1e293b',
                      border: '1px solid #475569',
                      borderRadius: '8px',
                      cursor: 'pointer',
                      fontSize: '1rem',
                      color: '#94a3b8',
                      transition: 'all 0.2s',
                      marginLeft: '1rem'
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.backgroundColor = '#334155'
                      e.currentTarget.style.color = '#e2e8f0'
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.backgroundColor = '#1e293b'
                      e.currentTarget.style.color = '#94a3b8'
                    }}
                  >
                    ◀
                  </button>
                </div>
              </div>
              )}

              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', flex: 1 }}>
                <div>
                  <div style={{
                    fontSize: '0.8rem',
                    fontWeight: 600,
                    color: '#60a5fa',
                    marginBottom: '0.75rem',
                    textTransform: 'uppercase',
                    letterSpacing: '1px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between'
                  }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <span style={{
                        display: 'inline-block',
                        width: '20px',
                        height: '2px',
                        background: 'linear-gradient(90deg, #60a5fa 0%, transparent 100%)'
                      }} />
                      제안요청내용
                      <span style={{
                        fontSize: '0.75rem',
                        color: '#94a3b8',
                        fontWeight: 500,
                        marginLeft: '0.5rem'
                      }}>
                        [{req.requirementId}]
                      </span>
                    </div>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      {editingIndex === index ? (
                        <>
                          <button
                            onClick={() => saveEditing(req, index)}
                            style={{
                              padding: '0.5rem 1rem',
                              background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
                              color: 'white',
                              border: 'none',
                              borderRadius: '8px',
                              cursor: 'pointer',
                              fontSize: '0.8rem',
                              fontWeight: 600,
                              whiteSpace: 'nowrap',
                              transition: 'all 0.3s'
                            }}
                          >
                            💾 저장
                          </button>
                          <button
                            onClick={cancelEditing}
                            style={{
                              padding: '0.5rem 1rem',
                              background: 'linear-gradient(135deg, #64748b 0%, #475569 100%)',
                              color: 'white',
                              border: 'none',
                              borderRadius: '8px',
                              cursor: 'pointer',
                              fontSize: '0.8rem',
                              fontWeight: 600,
                              whiteSpace: 'nowrap',
                              transition: 'all 0.3s'
                            }}
                          >
                            ✕ 취소
                          </button>
                        </>
                      ) : (
                        <>
                          <button
                            onClick={() => startEditing(index, req)}
                            style={{
                              padding: '0.5rem 1rem',
                              background: 'linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)',
                              color: 'white',
                              border: 'none',
                              borderRadius: '8px',
                              cursor: 'pointer',
                              fontSize: '0.8rem',
                              fontWeight: 600,
                              whiteSpace: 'nowrap',
                              transition: 'all 0.3s'
                            }}
                          >
                            ✏️ 수정
                          </button>
                          <button
                            id={`btn-${index}`}
                            onClick={() => generateTaskCards(req, index)}
                            disabled={generatingTasks.has(index)}
                            style={{
                              padding: '0.5rem 1rem',
                              background: generatingTasks.has(index)
                                ? 'linear-gradient(135deg, #64748b 0%, #475569 100%)'
                                : 'linear-gradient(135deg, #3b82f6 0%, #1e40af 100%)',
                              color: 'white',
                              border: 'none',
                              borderRadius: '8px',
                              cursor: generatingTasks.has(index) ? 'not-allowed' : 'pointer',
                              fontSize: '0.8rem',
                              fontWeight: 600,
                              whiteSpace: 'nowrap',
                              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                              boxShadow: '0 2px 8px rgba(59, 130, 246, 0.3)',
                              position: 'relative',
                              overflow: 'hidden',
                              opacity: generatingTasks.has(index) ? 0.7 : 1,
                              display: 'flex',
                              alignItems: 'center',
                              gap: '0.5rem'
                            }}
                            onMouseEnter={(e) => {
                              if (!generatingTasks.has(index)) {
                                e.currentTarget.style.transform = 'translateY(-2px)'
                                e.currentTarget.style.boxShadow = '0 4px 12px rgba(59, 130, 246, 0.4)'
                              }
                            }}
                            onMouseLeave={(e) => {
                              if (!generatingTasks.has(index)) {
                                e.currentTarget.style.transform = 'translateY(0)'
                                e.currentTarget.style.boxShadow = '0 2px 8px rgba(59, 130, 246, 0.3)'
                              }
                            }}
                          >
                            {generatingTasks.has(index) && (
                              <span style={{
                                animation: 'spin 1s linear infinite',
                                display: 'inline-block'
                              }}>⚙️</span>
                            )}
                            <span>
                              {generatingTasks.has(index) ? '생성 중...' : '✨ AI로 과업 생성'}
                            </span>
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                  {editingIndex === index ? (
                    <textarea
                      value={editingContent}
                      onChange={(e) => setEditingContent(e.target.value)}
                      style={{
                        width: '100%',
                        minHeight: '200px',
                        whiteSpace: 'pre-wrap',
                        lineHeight: '1.8',
                        color: '#e2e8f0',
                        backgroundColor: 'rgba(30, 41, 59, 0.8)',
                        padding: '1.25rem',
                        borderRadius: '12px',
                        border: '2px solid #8b5cf6',
                        borderLeft: '4px solid #8b5cf6',
                        fontSize: '0.95rem',
                        backdropFilter: 'blur(10px)',
                        fontFamily: 'inherit',
                        resize: 'vertical'
                      }}
                    />
                  ) : (
                    <div style={{
                      whiteSpace: 'pre-wrap',
                      lineHeight: '1.8',
                      color: '#e2e8f0',
                      backgroundColor: 'rgba(30, 41, 59, 0.5)',
                      padding: '1.25rem',
                      borderRadius: '12px',
                      border: '1px solid rgba(51, 65, 85, 0.5)',
                      borderLeft: '4px solid #60a5fa',
                      fontSize: '0.95rem',
                      backdropFilter: 'blur(10px)'
                    }}>
                      {req.requestContent}
                    </div>
                  )}
                </div>

                <div>
                  {/* 상세 정보는 항상 표시 */}
                  {true && (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '1rem' }}>
                      {/* 기능 제공 기한 */}
                      {(editingIndex === index || req.deadline) && (
                        <div>
                          <div style={{
                            fontSize: '0.85rem',
                            fontWeight: 600,
                            color: '#60a5fa',
                            marginBottom: '0.5rem'
                          }}>
                            기능 제공 기한
                          </div>
                          {editingIndex === index ? (
                            <input
                              type="text"
                              value={editingDeadline}
                              onChange={(e) => setEditingDeadline(e.target.value)}
                              placeholder="예: 2024-12-31"
                              style={{
                                width: '100%',
                                fontSize: '0.95rem',
                                color: '#e2e8f0',
                                padding: '0.75rem 1rem',
                                backgroundColor: 'rgba(30, 41, 59, 0.8)',
                                border: '2px solid #8b5cf6',
                                borderRadius: '8px',
                                fontFamily: 'inherit'
                              }}
                            />
                          ) : (
                            <div style={{
                              fontSize: '0.95rem',
                              color: '#cbd5e1',
                              padding: '0.5rem 1rem',
                              backgroundColor: '#1e293b',
                              border: '1px solid #334155',
                              borderRadius: '8px'
                            }}>
                              {req.deadline}
                            </div>
                          )}
                        </div>
                      )}

                      {/* 이행 의견 */}
                      {(editingIndex === index || req.implementationOpinion) && (
                        <div>
                          <div style={{
                            fontSize: '0.85rem',
                            fontWeight: 600,
                            color: '#60a5fa',
                            marginBottom: '0.5rem'
                          }}>
                            이행 의견
                          </div>
                          {editingIndex === index ? (
                            <textarea
                              value={editingImplementationOpinion}
                              onChange={(e) => setEditingImplementationOpinion(e.target.value)}
                              placeholder="이행 의견을 입력하세요"
                              style={{
                                width: '100%',
                                minHeight: '80px',
                                whiteSpace: 'pre-wrap',
                                fontSize: '0.95rem',
                                color: '#e2e8f0',
                                padding: '0.75rem 1rem',
                                backgroundColor: 'rgba(30, 41, 59, 0.8)',
                                border: '2px solid #8b5cf6',
                                borderRadius: '8px',
                                lineHeight: '1.6',
                                fontFamily: 'inherit',
                                resize: 'vertical'
                              }}
                            />
                          ) : (
                            <div style={{
                              whiteSpace: 'pre-wrap',
                              fontSize: '0.95rem',
                              color: '#cbd5e1',
                              padding: '0.75rem 1rem',
                              backgroundColor: '#1e293b',
                              border: '1px solid #334155',
                              borderRadius: '8px',
                              lineHeight: '1.6'
                            }}>
                              {req.implementationOpinion}
                            </div>
                          )}
                        </div>
                      )}

                      {/* PO/BA 의견 */}
                      {(editingIndex === index || req.pobaOpinion) && (
                        <div>
                          <div style={{
                            fontSize: '0.85rem',
                            fontWeight: 600,
                            color: '#60a5fa',
                            marginBottom: '0.5rem'
                          }}>
                            PO/BA 의견
                          </div>
                          {editingIndex === index ? (
                            <textarea
                              value={editingPobaOpinion}
                              onChange={(e) => setEditingPobaOpinion(e.target.value)}
                              placeholder="PO/BA 의견을 입력하세요"
                              style={{
                                width: '100%',
                                minHeight: '80px',
                                whiteSpace: 'pre-wrap',
                                fontSize: '0.95rem',
                                color: '#e2e8f0',
                                padding: '0.75rem 1rem',
                                backgroundColor: 'rgba(30, 41, 59, 0.8)',
                                border: '2px solid #8b5cf6',
                                borderRadius: '8px',
                                lineHeight: '1.6',
                                fontFamily: 'inherit',
                                resize: 'vertical'
                              }}
                            />
                          ) : (
                            <div style={{
                              whiteSpace: 'pre-wrap',
                              fontSize: '0.95rem',
                              color: '#cbd5e1',
                              padding: '0.75rem 1rem',
                              backgroundColor: '#1e293b',
                              border: '1px solid #334155',
                              borderRadius: '8px',
                              lineHeight: '1.6'
                            }}>
                              {req.pobaOpinion}
                            </div>
                          )}
                        </div>
                      )}

                      {/* 기술혁신 의견 */}
                      {(editingIndex === index || req.techInnovationOpinion) && (
                        <div>
                          <div style={{
                            fontSize: '0.85rem',
                            fontWeight: 600,
                            color: '#60a5fa',
                            marginBottom: '0.5rem'
                          }}>
                            기술혁신 의견
                          </div>
                          {editingIndex === index ? (
                            <textarea
                              value={editingTechInnovationOpinion}
                              onChange={(e) => setEditingTechInnovationOpinion(e.target.value)}
                              placeholder="기술혁신 의견을 입력하세요"
                              style={{
                                width: '100%',
                                minHeight: '80px',
                                whiteSpace: 'pre-wrap',
                                fontSize: '0.95rem',
                                color: '#e2e8f0',
                                padding: '0.75rem 1rem',
                                backgroundColor: 'rgba(30, 41, 59, 0.8)',
                                border: '2px solid #8b5cf6',
                                borderRadius: '8px',
                                lineHeight: '1.6',
                                fontFamily: 'inherit',
                                resize: 'vertical'
                              }}
                            />
                          ) : (
                            <div style={{
                              whiteSpace: 'pre-wrap',
                              fontSize: '0.95rem',
                              color: '#cbd5e1',
                              padding: '0.75rem 1rem',
                              backgroundColor: '#1e293b',
                              border: '1px solid #334155',
                              borderRadius: '8px',
                              lineHeight: '1.6'
                            }}>
                              {req.techInnovationOpinion}
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  )}
                </div>
              </div>
                </div>

                {/* 과업 생성 중이거나 과업이 있을 때만 리사이저 표시 */}
                {(hasAnyTaskCards || generatingTasks.has(index)) && (
                  <>
                    {/* 리사이저 */}
                    <div
                      onMouseDown={handleMouseDown}
                      style={{
                        width: '16px',
                        cursor: 'col-resize',
                        backgroundColor: 'transparent',
                        transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                        flexShrink: 0,
                        position: 'relative',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                      }}
                      onMouseEnter={(e) => {
                        const indicator = e.currentTarget.querySelector('.resize-indicator') as HTMLElement
                        if (indicator) {
                          indicator.style.opacity = '1'
                          indicator.style.transform = 'translate(-50%, -50%) scaleY(1.2)'
                        }
                      }}
                      onMouseLeave={(e) => {
                        if (!isResizing) {
                          const indicator = e.currentTarget.querySelector('.resize-indicator') as HTMLElement
                          if (indicator) {
                            indicator.style.opacity = '0'
                            indicator.style.transform = 'translate(-50%, -50%) scaleY(1)'
                          }
                        }
                      }}
                    >
                      <div
                        className="resize-indicator"
                        style={{
                          position: 'absolute',
                          top: '50%',
                          left: '50%',
                          transform: 'translate(-50%, -50%)',
                          width: '3px',
                          height: '60px',
                          background: isResizing
                            ? 'linear-gradient(180deg, transparent 0%, #3b82f6 20%, #3b82f6 80%, transparent 100%)'
                            : 'linear-gradient(180deg, transparent 0%, #60a5fa 20%, #60a5fa 80%, transparent 100%)',
                          borderRadius: '3px',
                          opacity: isResizing ? '1' : '0',
                          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                          boxShadow: isResizing ? '0 0 12px rgba(59, 130, 246, 0.6)' : '0 0 8px rgba(96, 165, 250, 0.4)',
                          pointerEvents: 'none'
                        }}
                      />
                    </div>

                    {/* 과업 영역 */}
                    {generatingTasks.has(index) && relatedTasks.length === 0 ? (
                      <div style={{
                        flex: 1,
                        paddingLeft: '2rem'
                      }}>
                        <div style={{
                          width: '100%',
                          background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
                          color: 'white',
                          padding: '2rem 3rem',
                          borderRadius: '16px',
                          fontSize: '1.1rem',
                          fontWeight: 600,
                          boxShadow: '0 8px 24px rgba(0,0,0,0.5), 0 0 0 1px rgba(148, 163, 184, 0.1)',
                          textAlign: 'center',
                          letterSpacing: '0.3px',
                          position: 'relative',
                          overflow: 'hidden'
                        }}>
                          <div style={{
                            position: 'absolute',
                            top: 0,
                            left: 0,
                            width: '100%',
                            height: '100%',
                            background: 'linear-gradient(90deg, transparent 0%, rgba(96, 165, 250, 0.1) 50%, transparent 100%)',
                            animation: 'shimmer 2s infinite',
                            pointerEvents: 'none'
                          }} />
                          <span style={{ position: 'relative', zIndex: 1 }}>
                            ✨ {generationStatus.get(index) || 'AI 분석 중...'}
                          </span>
                        </div>
                      </div>
                    ) : (relatedTasks.length > 0 || generatingTasks.has(index)) && (
                    <div style={{
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '1rem',
                      flex: 1,
                      minWidth: '400px',
                      padding: '0 0 0 2rem'
                    }}>
                    {/* 상태 표시 또는 과업 헤더 */}
                    {generatingTasks.has(index) ? (
                      <div style={{
                        background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
                        color: 'white',
                        padding: '1.25rem 2rem',
                        borderRadius: '16px',
                        fontSize: '0.95rem',
                        fontWeight: 600,
                        boxShadow: '0 6px 20px rgba(0, 0, 0, 0.4), 0 0 0 1px rgba(148, 163, 184, 0.2)',
                        textAlign: 'center',
                        letterSpacing: '0.3px',
                        position: 'relative',
                        overflow: 'hidden'
                      }}>
                        <div style={{
                          position: 'absolute',
                          top: 0,
                          left: 0,
                          width: '100%',
                          height: '100%',
                          background: 'linear-gradient(90deg, transparent 0%, rgba(96, 165, 250, 0.1) 50%, transparent 100%)',
                          animation: 'shimmer 2s infinite',
                          pointerEvents: 'none'
                        }} />
                        <span style={{ position: 'relative', zIndex: 1 }}>
                          ✨ {generationStatus.get(index) || 'AI 분석 중...'}
                        </span>
                      </div>
                    ) : (
                    <div
                      onClick={() => toggleTaskView(index)}
                      style={{
                        background: 'linear-gradient(135deg, #3b82f6 0%, #1e40af 100%)',
                        color: 'white',
                        padding: '1.25rem 2rem',
                        borderRadius: '16px',
                        fontSize: '1rem',
                        fontWeight: 700,
                        boxShadow: '0 6px 20px rgba(59, 130, 246, 0.5), 0 0 0 1px rgba(59, 130, 246, 0.3)',
                        textAlign: 'center',
                        letterSpacing: '0.3px',
                        cursor: 'pointer',
                        transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                        border: 'none',
                        position: 'relative',
                        overflow: 'hidden'
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.transform = 'translateY(-3px) scale(1.02)'
                        e.currentTarget.style.boxShadow = '0 10px 30px rgba(59, 130, 246, 0.6), 0 0 0 1px rgba(59, 130, 246, 0.4)'
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.transform = 'translateY(0) scale(1)'
                        e.currentTarget.style.boxShadow = '0 6px 20px rgba(59, 130, 246, 0.5), 0 0 0 1px rgba(59, 130, 246, 0.3)'
                      }}
                    >
                      <div style={{
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                        background: 'linear-gradient(135deg, rgba(255,255,255,0.1) 0%, transparent 100%)',
                        pointerEvents: 'none'
                      }} />
                      <span style={{ position: 'relative', zIndex: 1 }}>
                        {collapsedTaskViews.has(index)
                          ? `✨ ${req.requirementId}의 과업 ${relatedTasks.length}개 (클릭하여 열기)`
                          : `✨ ${req.requirementId}의 과업 ${relatedTasks.length}개 (클릭하여 닫기)`
                        }
                      </span>
                    </div>
                    )}
                    {!collapsedTaskViews.has(index) && relatedTasks.length > 0 && (
                    <>
                    {relatedTasks.map((task, taskIndex) => (
                      <div
                        key={task.id}
                        style={{
                          background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
                          border: '1px solid rgba(51, 65, 85, 0.5)',
                          borderRadius: '16px',
                          padding: '1.75rem',
                          boxShadow: '0 8px 24px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(59, 130, 246, 0.1)',
                          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                          position: 'relative',
                          overflow: 'hidden',
                          animation: 'fadeInUp 0.5s ease-out',
                          animationDelay: `${taskIndex * 0.1}s`,
                          animationFillMode: 'backwards'
                        }}
                        onMouseEnter={(e) => {
                          e.currentTarget.style.boxShadow = '0 12px 32px rgba(0, 0, 0, 0.6), 0 0 0 1px rgba(59, 130, 246, 0.3)'
                          e.currentTarget.style.transform = 'translateY(-4px)'
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.boxShadow = '0 8px 24px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(59, 130, 246, 0.1)'
                          e.currentTarget.style.transform = 'translateY(0)'
                        }}
                      >
                        <div style={{
                          position: 'absolute',
                          top: 0,
                          left: 0,
                          width: '100%',
                          height: '3px',
                          background: 'linear-gradient(90deg, #3b82f6 0%, #1e40af 50%, transparent 100%)',
                          opacity: 0.8
                        }} />

                        <div style={{
                          marginBottom: '1rem',
                          paddingBottom: '1rem',
                          borderBottom: '1px dashed #334155',
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center'
                        }}>
                          <div style={{
                            display: 'inline-block',
                            background: 'linear-gradient(135deg, #3b82f6 0%, #1e40af 100%)',
                            color: 'white',
                            padding: '0.25rem 0.75rem',
                            borderRadius: '20px',
                            fontSize: '0.75rem',
                            fontWeight: 700,
                            letterSpacing: '0.5px',
                            boxShadow: '0 2px 4px rgba(59, 130, 246, 0.3)'
                          }}>
                            TASK #{taskIndex + 1}
                          </div>
                          <div style={{
                            fontWeight: 700,
                            color: '#60a5fa',
                            fontSize: '1.1rem'
                          }}>
                            {task.id}
                          </div>
                        </div>

                        <div style={{ marginBottom: '1rem' }}>
                          <div style={{
                            fontSize: '0.75rem',
                            color: '#94a3b8',
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
                              background: 'linear-gradient(135deg, #3b82f6 0%, #1e40af 100%)',
                              borderRadius: '2px'
                            }} />
                            과업 내용 요약
                          </div>
                          <div style={{
                            fontSize: '0.95rem',
                            color: '#cbd5e1',
                            padding: '1rem 1.25rem',
                            backgroundColor: '#1e293b',
                            borderRadius: '8px',
                            border: '2px solid #334155',
                            lineHeight: '1.7',
                            fontWeight: 500
                          }}>
                            {task.summary}
                          </div>
                        </div>

                        <div style={{
                          backgroundColor: '#1e293b',
                          borderRadius: '8px',
                          padding: '1rem',
                          marginBottom: '1rem',
                          display: 'grid',
                          gridTemplateColumns: '1fr 1fr',
                          gap: '1rem',
                          border: '1px solid #334155'
                        }}>
                          <div>
                            <div style={{ fontSize: '0.75rem', color: '#94a3b8', marginBottom: '0.25rem', fontWeight: 600, textTransform: 'uppercase' }}>
                              기능 대분류
                            </div>
                            <div style={{ fontSize: '1rem', fontWeight: 700, color: '#e2e8f0' }}>
                              {task.majorCategory}
                            </div>
                            <div style={{ fontSize: '0.75rem', color: '#60a5fa', marginTop: '0.25rem' }}>
                              [{task.majorCategoryId}]
                            </div>
                          </div>

                          <div>
                            <div style={{ fontSize: '0.75rem', color: '#94a3b8', marginBottom: '0.25rem', fontWeight: 600, textTransform: 'uppercase' }}>
                              상세 기능
                            </div>
                            <div style={{ fontSize: '1rem', fontWeight: 700, color: '#e2e8f0' }}>
                              {task.detailFunction}
                            </div>
                            <div style={{ fontSize: '0.75rem', color: '#60a5fa', marginTop: '0.25rem' }}>
                              [{task.detailFunctionId}]
                            </div>
                          </div>
                        </div>

                        <div style={{
                          backgroundColor: '#0f172a',
                          padding: '0.75rem 1rem',
                          borderRadius: '8px',
                          border: '1px solid #334155'
                        }}>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8', marginBottom: '0.5rem', fontWeight: 600, textTransform: 'uppercase' }}>
                            세부 기능
                          </div>
                          <div style={{ fontSize: '1.05rem', color: '#cbd5e1', fontWeight: 500, lineHeight: '1.6' }}>
                            {task.subFunction}
                          </div>
                        </div>

                        {/* 생성 모델 정보 */}
                        <div style={{
                          marginTop: '0.75rem',
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center',
                          gap: '0.5rem'
                        }}>
                          {task.generatedBy && (
                            <div style={{
                              padding: '0.3rem 0.6rem',
                              background: 'rgba(139, 92, 246, 0.08)',
                              border: '1px solid rgba(139, 92, 246, 0.25)',
                              borderRadius: '6px',
                              display: 'flex',
                              alignItems: 'center',
                              gap: '0.4rem'
                            }}>
                              <span style={{ fontSize: '0.75rem' }}>✨</span>
                              <span style={{
                                fontSize: '0.7rem',
                                color: '#a78bfa',
                                fontWeight: 600
                              }}>
                                {task.generatedBy}
                              </span>
                            </div>
                          )}
                          {task.createdAt && (
                            <div style={{
                              fontSize: '0.65rem',
                              color: '#64748b',
                              fontWeight: 500
                            }}>
                              {new Date(task.createdAt).toLocaleString('ko-KR', {
                                year: 'numeric',
                                month: '2-digit',
                                day: '2-digit',
                                hour: '2-digit',
                                minute: '2-digit',
                                second: '2-digit'
                              })}
                            </div>
                          )}
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
        )}
      </main>
    </div>
  )
}

export default App
