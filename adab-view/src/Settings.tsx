import { useState, useEffect } from 'react'
import { getModelConfigs, updateModelConfig, type ModelConfig } from './api'

interface SettingsProps {
  onBack?: () => void
}

function Settings({ onBack }: SettingsProps = {}) {
  const [configs, setConfigs] = useState<ModelConfig[]>([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState<string | null>(null)
  const [editingConfig, setEditingConfig] = useState<ModelConfig | null>(null)
  const [testing, setTesting] = useState<string | null>(null)

  const modelTypes = [
    { name: 'ollama', label: 'Ollama', icon: '🦙' },
    { name: 'claude', label: 'Claude (Anthropic)', icon: '🤖' },
    { name: 'gemini', label: 'Gemini (Google)', icon: '✨' },
    { name: 'openai', label: 'OpenAI', icon: '🔮' }
  ]

  useEffect(() => {
    fetchConfigs()
  }, [])

  const fetchConfigs = async () => {
    try {
      setLoading(true)
      const data = await getModelConfigs()
      setConfigs(data)
    } catch (err) {
      console.error('설정 로딩 실패:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleSave = async (name: string, updates: Partial<ModelConfig>) => {
    try {
      setSaving(name)
      await updateModelConfig(name, updates)
      await fetchConfigs()
      setEditingConfig(null)

      // onBack이 있으면 (Settings 페이지에서 호출) 부모 컴포넌트에 모델 변경 알림
      if (onBack) {
        onBack()
      }

      alert('✅ 설정이 저장되었습니다.')
    } catch (err: any) {
      console.error('설정 저장 실패:', err)
      alert(`❌ 저장 실패: ${err.response?.data?.message || err.message}`)
    } finally {
      setSaving(null)
    }
  }

  const handleToggleEnabled = async (name: string, currentEnabled: boolean) => {
    await handleSave(name, { enabled: !currentEnabled })
  }

  const handleSetDefault = async (name: string) => {
    await handleSave(name, { isDefault: true, enabled: true })
  }

  const getConfigByName = (name: string): ModelConfig | undefined => {
    return configs.find(c => c.name === name)
  }

  const getDefaultModelName = (name: string): string => {
    switch (name) {
      case 'ollama': return 'gemma3:12b'
      case 'claude': return 'claude-3-5-sonnet-20240620'
      case 'gemini': return 'gemini-pro'
      case 'openai': return 'gpt-4'
      default: return ''
    }
  }

  const getDefaultBaseUrl = (name: string): string => {
    switch (name) {
      case 'ollama': return 'http://localhost:11434'
      case 'claude': return 'https://api.anthropic.com'
      default: return ''
    }
  }

  const startEditing = (name: string) => {
    const config = getConfigByName(name)
    if (config) {
      setEditingConfig({ ...config })
    } else {
      // 새로 생성
      setEditingConfig({
        name,
        enabled: false,
        isDefault: false,
        modelName: getDefaultModelName(name),
        apiKey: '',
        baseUrl: getDefaultBaseUrl(name),
        temperature: '0.7',
        maxTokens: name === 'claude' ? '4096' : '2048'
      })
    }
  }

  const cancelEditing = () => {
    setEditingConfig(null)
  }

  const saveEditing = () => {
    if (editingConfig) {
      handleSave(editingConfig.name, editingConfig)
    }
  }

  const handleTestConnection = async (name: string) => {
    const config = getConfigByName(name)
    if (!config) {
      alert('❌ 설정을 먼저 추가해주세요.')
      return
    }

    try {
      setTesting(name)
      const response = await fetch('/api/model-configs/test', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name })
      })

      const result = await response.json()

      if (response.ok && result.success) {
        alert(`✅ 연결 성공!\n\n모델: ${result.modelName || 'N/A'}\n응답: ${result.testResponse || 'OK'}`)
      } else {
        alert(`❌ 연결 실패\n\n${result.error || '알 수 없는 오류'}`)
      }
    } catch (err: any) {
      console.error('테스트 실패:', err)
      alert(`❌ 연결 테스트 실패\n\n${err.message}`)
    } finally {
      setTesting(null)
    }
  }

  if (loading) {
    return (
      <div style={{
        width: '100vw',
        height: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#1e293b'
      }}>
        <div style={{
          background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
          color: 'white',
          padding: '2rem 3rem',
          borderRadius: '16px',
          fontSize: '1.1rem',
          fontWeight: 600,
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)'
        }}>
          ⚙️ 설정을 불러오는 중...
        </div>
      </div>
    )
  }

  return (
    <div style={{
      width: '100vw',
      height: '100vh',
      display: 'flex',
      flexDirection: 'column',
      backgroundColor: '#1e293b'
    }}>
      <header style={{
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
        color: 'white',
        padding: '1.25rem 2rem',
        borderBottom: '1px solid rgba(148, 163, 184, 0.2)',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        boxShadow: '0 4px 20px rgba(0,0,0,0.4)'
      }}>
        <h1 style={{
          margin: 0,
          fontSize: '1.5rem',
          fontWeight: 700,
          background: 'linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent'
        }}>⚙️ AI 모델 설정</h1>
        <button
          onClick={onBack}
          style={{
            padding: '0.5rem 1rem',
            background: 'linear-gradient(135deg, #64748b 0%, #475569 100%)',
            color: 'white',
            border: 'none',
            borderRadius: '6px',
            cursor: 'pointer',
            fontSize: '0.85rem',
            fontWeight: 600
          }}
        >
          ← 돌아가기
        </button>
      </header>

      <main style={{
        flex: 1,
        padding: '2rem',
        overflowY: 'auto'
      }}>
        <div style={{
          maxWidth: '1200px',
          margin: '0 auto',
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(500px, 1fr))',
          gap: '1.5rem'
        }}>
          {modelTypes.map(model => {
            const config = getConfigByName(model.name)
            const isEditing = editingConfig?.name === model.name

            return (
              <div
                key={model.name}
                style={{
                  background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
                  border: config?.isDefault
                    ? '2px solid #3b82f6'
                    : '1px solid rgba(51, 65, 85, 0.5)',
                  borderRadius: '16px',
                  padding: '2rem',
                  boxShadow: config?.isDefault
                    ? '0 8px 24px rgba(59, 130, 246, 0.3)'
                    : '0 8px 24px rgba(0,0,0,0.5)',
                  position: 'relative'
                }}
              >
                {config?.isDefault && (
                  <div style={{
                    position: 'absolute',
                    top: '1rem',
                    right: '1rem',
                    background: 'linear-gradient(135deg, #3b82f6 0%, #1e40af 100%)',
                    color: 'white',
                    padding: '0.35rem 0.75rem',
                    borderRadius: '20px',
                    fontSize: '0.7rem',
                    fontWeight: 700,
                    letterSpacing: '0.5px'
                  }}>
                    기본 모델
                  </div>
                )}

                <div style={{
                  fontSize: '2rem',
                  marginBottom: '0.5rem'
                }}>
                  {model.icon}
                </div>

                <h2 style={{
                  margin: '0 0 0.5rem 0',
                  fontSize: '1.3rem',
                  fontWeight: 700,
                  color: '#e2e8f0'
                }}>
                  {model.label}
                </h2>

                <div style={{
                  marginBottom: '1.5rem',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.75rem'
                }}>
                  <span style={{
                    fontSize: '0.85rem',
                    color: '#94a3b8',
                    fontWeight: 500
                  }}>
                    상태:
                  </span>
                  <span style={{
                    padding: '0.25rem 0.75rem',
                    borderRadius: '12px',
                    fontSize: '0.75rem',
                    fontWeight: 600,
                    background: config?.enabled
                      ? 'rgba(34, 197, 94, 0.2)'
                      : 'rgba(239, 68, 68, 0.2)',
                    color: config?.enabled ? '#22c55e' : '#ef4444',
                    border: `1px solid ${config?.enabled ? '#22c55e' : '#ef4444'}`
                  }}>
                    {config?.enabled ? '활성화됨' : '비활성화됨'}
                  </span>
                </div>

                {isEditing ? (
                  <div style={{
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '1rem'
                  }}>
                    <div>
                      <label style={{
                        display: 'block',
                        fontSize: '0.8rem',
                        color: '#94a3b8',
                        marginBottom: '0.5rem',
                        fontWeight: 600
                      }}>
                        모델명
                      </label>
                      <input
                        type="text"
                        value={editingConfig.modelName || ''}
                        onChange={(e) => setEditingConfig({ ...editingConfig, modelName: e.target.value })}
                        placeholder={model.name === 'ollama' ? 'gemma3:12b' : model.name === 'claude' ? 'claude-3-5-sonnet-20240620' : model.name === 'gemini' ? 'gemini-pro' : 'gpt-4'}
                        style={{
                          width: '100%',
                          padding: '0.75rem',
                          background: '#1e293b',
                          border: '1px solid #475569',
                          borderRadius: '8px',
                          color: '#e2e8f0',
                          fontSize: '0.9rem',
                          fontFamily: 'inherit'
                        }}
                      />
                    </div>

                    {model.name !== 'ollama' && (
                      <div>
                        <label style={{
                          display: 'block',
                          fontSize: '0.8rem',
                          color: '#94a3b8',
                          marginBottom: '0.5rem',
                          fontWeight: 600
                        }}>
                          API Key
                        </label>
                        <input
                          type="password"
                          value={editingConfig.apiKey || ''}
                          onChange={(e) => setEditingConfig({ ...editingConfig, apiKey: e.target.value })}
                          placeholder="API 키를 입력하세요"
                          style={{
                            width: '100%',
                            padding: '0.75rem',
                            background: '#1e293b',
                            border: '1px solid #475569',
                            borderRadius: '8px',
                            color: '#e2e8f0',
                            fontSize: '0.9rem',
                            fontFamily: 'inherit'
                          }}
                        />
                      </div>
                    )}

                    <div>
                      <label style={{
                        display: 'block',
                        fontSize: '0.8rem',
                        color: '#94a3b8',
                        marginBottom: '0.5rem',
                        fontWeight: 600
                      }}>
                        Base URL
                      </label>
                      <input
                        type="text"
                        value={editingConfig.baseUrl || ''}
                        onChange={(e) => setEditingConfig({ ...editingConfig, baseUrl: e.target.value })}
                        placeholder={model.name === 'ollama' ? 'http://localhost:11434' : '선택사항'}
                        style={{
                          width: '100%',
                          padding: '0.75rem',
                          background: '#1e293b',
                          border: '1px solid #475569',
                          borderRadius: '8px',
                          color: '#e2e8f0',
                          fontSize: '0.9rem',
                          fontFamily: 'inherit'
                        }}
                      />
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                      <div>
                        <label style={{
                          display: 'block',
                          fontSize: '0.8rem',
                          color: '#94a3b8',
                          marginBottom: '0.5rem',
                          fontWeight: 600
                        }}>
                          Temperature
                        </label>
                        <input
                          type="text"
                          value={editingConfig.temperature || ''}
                          onChange={(e) => setEditingConfig({ ...editingConfig, temperature: e.target.value })}
                          placeholder="0.7"
                          style={{
                            width: '100%',
                            padding: '0.75rem',
                            background: '#1e293b',
                            border: '1px solid #475569',
                            borderRadius: '8px',
                            color: '#e2e8f0',
                            fontSize: '0.9rem',
                            fontFamily: 'inherit'
                          }}
                        />
                      </div>
                      <div>
                        <label style={{
                          display: 'block',
                          fontSize: '0.8rem',
                          color: '#94a3b8',
                          marginBottom: '0.5rem',
                          fontWeight: 600
                        }}>
                          Max Tokens
                        </label>
                        <input
                          type="text"
                          value={editingConfig.maxTokens || ''}
                          onChange={(e) => setEditingConfig({ ...editingConfig, maxTokens: e.target.value })}
                          placeholder="2048"
                          style={{
                            width: '100%',
                            padding: '0.75rem',
                            background: '#1e293b',
                            border: '1px solid #475569',
                            borderRadius: '8px',
                            color: '#e2e8f0',
                            fontSize: '0.9rem',
                            fontFamily: 'inherit'
                          }}
                        />
                      </div>
                    </div>

                    <div style={{
                      display: 'flex',
                      gap: '0.75rem',
                      marginTop: '0.5rem'
                    }}>
                      <button
                        onClick={saveEditing}
                        disabled={saving === model.name}
                        style={{
                          flex: 1,
                          padding: '0.75rem',
                          background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
                          color: 'white',
                          border: 'none',
                          borderRadius: '8px',
                          cursor: saving === model.name ? 'not-allowed' : 'pointer',
                          fontSize: '0.9rem',
                          fontWeight: 600,
                          opacity: saving === model.name ? 0.7 : 1
                        }}
                      >
                        {saving === model.name ? '저장 중...' : '💾 저장'}
                      </button>
                      <button
                        onClick={cancelEditing}
                        style={{
                          flex: 1,
                          padding: '0.75rem',
                          background: 'linear-gradient(135deg, #64748b 0%, #475569 100%)',
                          color: 'white',
                          border: 'none',
                          borderRadius: '8px',
                          cursor: 'pointer',
                          fontSize: '0.9rem',
                          fontWeight: 600
                        }}
                      >
                        ✕ 취소
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    {config && (
                      <div style={{
                        marginBottom: '1.5rem',
                        fontSize: '0.85rem',
                        color: '#cbd5e1',
                        display: 'flex',
                        flexDirection: 'column',
                        gap: '0.5rem'
                      }}>
                        {config.modelName && (
                          <div>
                            <span style={{ color: '#94a3b8', fontWeight: 600 }}>모델:</span>{' '}
                            {config.modelName}
                          </div>
                        )}
                        {config.baseUrl && (
                          <div>
                            <span style={{ color: '#94a3b8', fontWeight: 600 }}>URL:</span>{' '}
                            {config.baseUrl}
                          </div>
                        )}
                        {config.apiKey && (
                          <div>
                            <span style={{ color: '#94a3b8', fontWeight: 600 }}>API Key:</span>{' '}
                            {config.apiKey}
                          </div>
                        )}
                      </div>
                    )}

                    <div style={{
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '0.75rem'
                    }}>
                      <button
                        onClick={() => startEditing(model.name)}
                        style={{
                          padding: '0.75rem',
                          background: 'linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)',
                          color: 'white',
                          border: 'none',
                          borderRadius: '8px',
                          cursor: 'pointer',
                          fontSize: '0.9rem',
                          fontWeight: 600
                        }}
                      >
                        ✏️ {config ? '설정 수정' : '설정 추가'}
                      </button>

                      {config && (
                        <>
                          <button
                            onClick={() => handleTestConnection(model.name)}
                            disabled={testing === model.name}
                            style={{
                              padding: '0.75rem',
                              background: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)',
                              color: 'white',
                              border: 'none',
                              borderRadius: '8px',
                              cursor: testing === model.name ? 'not-allowed' : 'pointer',
                              fontSize: '0.9rem',
                              fontWeight: 600,
                              opacity: testing === model.name ? 0.7 : 1
                            }}
                          >
                            {testing === model.name ? '⏳ 테스트 중...' : '🔌 연결 테스트'}
                          </button>

                          <button
                            onClick={() => handleToggleEnabled(model.name, config.enabled || false)}
                            disabled={saving === model.name}
                            style={{
                              padding: '0.75rem',
                              background: config.enabled
                                ? 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)'
                                : 'linear-gradient(135deg, #22c55e 0%, #16a34a 100%)',
                              color: 'white',
                              border: 'none',
                              borderRadius: '8px',
                              cursor: saving === model.name ? 'not-allowed' : 'pointer',
                              fontSize: '0.9rem',
                              fontWeight: 600,
                              opacity: saving === model.name ? 0.7 : 1
                            }}
                          >
                            {config.enabled ? '⏸️ 비활성화' : '▶️ 활성화'}
                          </button>

                          {!config.isDefault && config.enabled && (
                            <button
                              onClick={() => handleSetDefault(model.name)}
                              disabled={saving === model.name}
                              style={{
                                padding: '0.75rem',
                                background: 'linear-gradient(135deg, #3b82f6 0%, #1e40af 100%)',
                                color: 'white',
                                border: 'none',
                                borderRadius: '8px',
                                cursor: saving === model.name ? 'not-allowed' : 'pointer',
                                fontSize: '0.9rem',
                                fontWeight: 600,
                                opacity: saving === model.name ? 0.7 : 1
                              }}
                            >
                              ⭐ 기본 모델로 설정
                            </button>
                          )}
                        </>
                      )}
                    </div>
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

export default Settings
