import { useState, useEffect } from 'react';
import { getModelConfigs, updateModelConfig, isAxiosError, type ModelConfig } from './api';
import { ModelCard } from './components/ModelCard';

interface SettingsProps {
  onBack?: () => void;
}

function Settings({ onBack }: SettingsProps = {}) {
  const [configs, setConfigs] = useState<ModelConfig[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState<string | null>(null);
  const [editingConfig, setEditingConfig] = useState<ModelConfig | null>(null);
  const [testing, setTesting] = useState<string | null>(null);

  const modelTypes = [
    { name: 'ollama', label: 'Ollama', icon: '🦙' },
    { name: 'claude', label: 'Claude (Anthropic)', icon: '🤖' },
    { name: 'gemini', label: 'Gemini (Google)', icon: '✨' },
    { name: 'openai', label: 'OpenAI', icon: '🔮' }
  ];

  useEffect(() => {
    fetchConfigs();
  }, []);

  const fetchConfigs = async () => {
    try {
      setLoading(true);
      const data = await getModelConfigs();
      setConfigs(data);
    } catch (err) {
      console.error('설정 로딩 실패:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (name: string, updates: Partial<ModelConfig>) => {
    try {
      setSaving(name);
      await updateModelConfig(name, updates);
      await fetchConfigs();
      setEditingConfig(null);
      if (onBack && updates.isDefault) onBack();
      alert('✅ 설정이 저장되었습니다.');
    } catch (err: unknown) {
      console.error('설정 저장 실패:', err);
      let message = '알 수 없는 에러가 발생했습니다.';
      if (isAxiosError(err)) {
        message = err.response?.data?.message || err.message;
      } else if (err instanceof Error) {
        message = err.message;
      }
      alert(`❌ 저장 실패: ${message}`);
    } finally {
      setSaving(null);
    }
  };

  const handleTestConnection = async (name: string) => {
    try {
      setTesting(name);
      const response = await fetch('http://localhost:8080/api/model-configs/test', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name })
      });
      const result = await response.json();
      if (response.ok && result.success) {
        alert(`✅ 연결 성공!\n\n모델: ${result.modelName || 'N/A'}\n응답: ${result.testResponse || 'OK'}`);
      } else {
        alert(`❌ 연결 실패\n\n${result.error || '알 수 없는 오류'}`);
      }
    } catch (err: unknown) {
      console.error('테스트 실패:', err);
      let message = '연결 테스트 중 오류가 발생했습니다.';
      if (err instanceof Error) {
        message = err.message;
      }
      alert(`❌ 연결 테스트 실패\n\n${message}`);
    } finally {
      setTesting(null);
    }
  };

  const startEditing = (name: string) => {
    const config = configs.find((c: ModelConfig) => c.name === name);
    if (config) {
      setEditingConfig({ ...config });
    } else {
      setEditingConfig({
        name,
        enabled: false,
        isDefault: false,
        modelName: name === 'ollama' ? 'gemma3:12b' : 
                   name === 'claude' ? 'claude-3-5-sonnet-20240620' : 
                   name === 'gemini' ? 'gemini-2.5-flash' : 'gpt-4',
        apiKey: '',
        baseUrl: name === 'ollama' ? 'http://localhost:11434' : 
                 name === 'claude' ? 'https://api.anthropic.com' : 
                 name === 'gemini' ? 'https://generativelanguage.googleapis.com' : '',
        temperature: '0.7',
        maxTokens: name === 'claude' ? '4096' : '4096'
      });
    }
  };

  if (loading) {
    return (
      <div style={{ width: '100vw', height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: '#1e293b' }}>
        <div style={{ background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)', color: 'white', padding: '2rem 3rem', borderRadius: '16px', fontSize: '1.1rem', fontWeight: 600, boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)' }}>
          ⚙️ 설정을 불러오는 중...
        </div>
      </div>
    );
  }

  return (
    <div style={{ width: '100vw', height: '100vh', display: 'flex', flexDirection: 'column', backgroundColor: '#1e293b' }}>
      <header style={{ background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)', color: 'white', padding: '1.25rem 2rem', borderBottom: '1px solid rgba(148, 163, 184, 0.2)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', boxShadow: '0 4px 20px rgba(0,0,0,0.4)' }}>
        <h1 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, background: 'linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>⚙️ LLM 설정</h1>
        <button onClick={onBack} style={{ padding: '0.5rem 1rem', background: 'linear-gradient(135deg, #64748b 0%, #475569 100%)', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '0.85rem', fontWeight: 600 }}>← 돌아가기</button>
      </header>

      <main style={{ flex: 1, padding: '2rem', overflowY: 'auto' }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(500px, 1fr))', gap: '1.5rem' }}>
          {modelTypes.map(model => (
            <ModelCard
              key={model.name}
              model={model}
              config={configs.find((c: ModelConfig) => c.name === model.name)}
              isEditing={editingConfig?.name === model.name}
              editingConfig={editingConfig}
              onStartEditing={startEditing}
              onCancelEditing={() => setEditingConfig(null)}
              onSaveEditing={() => editingConfig && handleSave(editingConfig.name, editingConfig)}
              onSetEditingConfig={setEditingConfig}
              onToggleEnabled={(name, enabled) => handleSave(name, { enabled: !enabled })}
              onSetDefault={(name) => handleSave(name, { isDefault: true, enabled: true })}
              onTestConnection={handleTestConnection}
              saving={saving}
              testing={testing}
            />
          ))}
        </div>
      </main>
    </div>
  );
}

export default Settings;
