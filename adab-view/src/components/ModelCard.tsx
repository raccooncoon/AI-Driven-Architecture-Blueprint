import React from 'react';
import type { ModelConfig } from '../api';

interface ModelCardProps {
  model: { name: string; label: string; icon: string };
  config?: ModelConfig;
  isEditing: boolean;
  editingConfig: ModelConfig | null;
  onStartEditing: (name: string) => void;
  onCancelEditing: () => void;
  onSaveEditing: () => void;
  onSetEditingConfig: (config: ModelConfig) => void;
  onToggleEnabled: (name: string, enabled: boolean) => void;
  onSetDefault: (name: string) => void;
  onTestConnection: (name: string) => void;
  saving: string | null;
  testing: string | null;
}

export const ModelCard: React.FC<ModelCardProps> = ({
  model,
  config,
  isEditing,
  editingConfig,
  onStartEditing,
  onCancelEditing,
  onSaveEditing,
  onSetEditingConfig,
  onToggleEnabled,
  onSetDefault,
  onTestConnection,
  saving,
  testing
}) => {
  const isSaving = saving === model.name;
  const isTesting = testing === model.name;

  return (
    <div
      style={{
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
        border: config?.isDefault ? '2px solid #3b82f6' : '1px solid rgba(51, 65, 85, 0.5)',
        borderRadius: '16px',
        padding: '2rem',
        boxShadow: config?.isDefault ? '0 8px 24px rgba(59, 130, 246, 0.3)' : '0 8px 24px rgba(0,0,0,0.5)',
        position: 'relative'
      }}
    >
      {config?.isDefault && (
        <div style={{
          position: 'absolute', top: '1rem', right: '1rem', background: 'linear-gradient(135deg, #3b82f6 0%, #1e40af 100%)',
          color: 'white', padding: '0.35rem 0.75rem', borderRadius: '20px', fontSize: '0.7rem', fontWeight: 700
        }}>
          기본 모델
        </div>
      )}

      <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>{model.icon}</div>
      <h2 style={{ margin: '0 0 0.5rem 0', fontSize: '1.3rem', fontWeight: 700, color: '#e2e8f0' }}>{model.label}</h2>

      <div style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        <span style={{ fontSize: '0.85rem', color: '#94a3b8', fontWeight: 500 }}>상태:</span>
        <span style={{
          padding: '0.25rem 0.75rem', borderRadius: '12px', fontSize: '0.75rem', fontWeight: 600,
          background: config?.enabled ? 'rgba(34, 197, 94, 0.2)' : 'rgba(239, 68, 68, 0.2)',
          color: config?.enabled ? '#22c55e' : '#ef4444',
          border: `1px solid ${config?.enabled ? '#22c55e' : '#ef4444'}`
        }}>
          {config?.enabled ? '활성화됨' : '비활성화됨'}
        </span>
      </div>

      {isEditing && editingConfig ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div>
            <label style={labelStyle}>모델명</label>
            <input
              type="text"
              style={inputStyle}
              value={editingConfig.modelName || ''}
              onChange={(e) => onSetEditingConfig({ ...editingConfig, modelName: e.target.value })}
            />
          </div>

          {model.name !== 'ollama' && (
            <div>
              <label style={labelStyle}>API Key</label>
              <input
                type="password"
                style={inputStyle}
                value={editingConfig.apiKey || ''}
                onChange={(e) => onSetEditingConfig({ ...editingConfig, apiKey: e.target.value })}
              />
            </div>
          )}

          <div>
            <label style={labelStyle}>Base URL</label>
            <input
              type="text"
              style={inputStyle}
              value={editingConfig.baseUrl || ''}
              onChange={(e) => onSetEditingConfig({ ...editingConfig, baseUrl: e.target.value })}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div>
              <label style={labelStyle}>Temperature</label>
              <input
                type="text"
                style={inputStyle}
                value={editingConfig.temperature || ''}
                onChange={(e) => onSetEditingConfig({ ...editingConfig, temperature: e.target.value })}
              />
            </div>
            <div>
              <label style={labelStyle}>Max Tokens</label>
              <input
                type="text"
                style={inputStyle}
                value={editingConfig.maxTokens || ''}
                onChange={(e) => onSetEditingConfig({ ...editingConfig, maxTokens: e.target.value })}
              />
            </div>
          </div>

          <div style={{ display: 'flex', gap: '0.75rem', marginTop: '0.5rem' }}>
            <button onClick={onSaveEditing} disabled={isSaving} style={{ ...buttonStyle, background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)', flex: 1 }}>
              {isSaving ? '저장 중...' : '💾 저장'}
            </button>
            <button onClick={onCancelEditing} style={{ ...buttonStyle, background: 'linear-gradient(135deg, #64748b 0%, #475569 100%)', flex: 1 }}>✕ 취소</button>
          </div>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
          {config && (
            <div style={{ marginBottom: '1.5rem', fontSize: '0.85rem', color: '#cbd5e1', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {config.modelName && <div><span style={{ color: '#94a3b8', fontWeight: 600 }}>모델:</span> {config.modelName}</div>}
              {config.baseUrl && <div><span style={{ color: '#94a3b8', fontWeight: 600 }}>URL:</span> {config.baseUrl}</div>}
              {config.apiKey && <div><span style={{ color: '#94a3b8', fontWeight: 600 }}>API Key:</span> {config.apiKey ? '********' : ''}</div>}
            </div>
          )}
          <button onClick={() => onStartEditing(model.name)} style={{ ...buttonStyle, background: 'linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)' }}>
            ✏️ {config ? '설정 수정' : '설정 추가'}
          </button>
          {config && (
            <>
              <button onClick={() => onTestConnection(model.name)} disabled={isTesting} style={{ ...buttonStyle, background: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)' }}>
                {isTesting ? '⏳ 테스트 중...' : '🔌 연결 테스트'}
              </button>
              <button onClick={() => onToggleEnabled(model.name, config.enabled || false)} disabled={isSaving} style={{ ...buttonStyle, background: config.enabled ? 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)' : 'linear-gradient(135deg, #22c55e 0%, #16a34a 100%)' }}>
                {config.enabled ? '⏸️ 비활성화' : '▶️ 활성화'}
              </button>
              {!config.isDefault && config.enabled && (
                <button onClick={() => onSetDefault(model.name)} disabled={isSaving} style={{ ...buttonStyle, background: 'linear-gradient(135deg, #3b82f6 0%, #1e40af 100%)' }}>⭐ 기본 모델로 설정</button>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
};

const labelStyle: React.CSSProperties = { display: 'block', fontSize: '0.8rem', color: '#94a3b8', marginBottom: '0.5rem', fontWeight: 600 };
const inputStyle: React.CSSProperties = { width: '100%', padding: '0.75rem', background: '#1e293b', border: '1px solid #475569', borderRadius: '8px', color: '#e2e8f0', fontSize: '0.9rem' };
const buttonStyle: React.CSSProperties = { padding: '0.75rem', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '0.9rem', fontWeight: 600, transition: 'all 0.3s' };
