import React from 'react';

interface HeaderProps {
  currentModel: string;
  onOpenSettings: () => void;
}

export const Header: React.FC<HeaderProps> = ({
  currentModel,
  onOpenSettings
}) => {
  return (
    <header style={{
      background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
      color: 'white',
      padding: '1.25rem 2rem',
      borderBottom: '1px solid rgba(148, 163, 184, 0.2)',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      boxShadow: '0 4px 20px rgba(0,0,0,0.4)',
      position: 'sticky',
      top: 0,
      zIndex: 1000,
      backdropFilter: 'blur(10px)'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
        <h1 style={{
          margin: 0,
          fontSize: '1.75rem',
          fontWeight: 800,
          background: 'linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          letterSpacing: '-0.5px'
        }}>
          ADAB <span style={{ fontSize: '0.9rem', color: '#94a3b8', WebkitTextFillColor: '#94a3b8', fontWeight: 500 }}>AI-Driven Architecture Blueprint</span>
        </h1>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.75rem',
          padding: '0.5rem 1rem',
          background: 'rgba(30, 41, 59, 0.5)',
          borderRadius: '12px',
          border: '1px solid rgba(148, 163, 184, 0.1)'
        }}>
          <span style={{ fontSize: '0.85rem', color: '#94a3b8', fontWeight: 600 }}>Active Model</span>
          <span style={{
            fontSize: '0.9rem',
            color: '#60a5fa',
            fontWeight: 700,
            padding: '0.2rem 0.6rem',
            background: 'rgba(59, 130, 246, 0.1)',
            borderRadius: '6px'
          }}>{currentModel || 'Loading...'}</span>
          <button
            onClick={onOpenSettings}
            style={{
              padding: '0.4rem 0.8rem',
              background: 'linear-gradient(135deg, #334155 0%, #1e293b 100%)',
              color: '#e2e8f0',
              border: '1px solid #475569',
              borderRadius: '8px',
              cursor: 'pointer',
              fontSize: '0.8rem',
              fontWeight: 600,
              transition: 'all 0.3s'
            }}
          >
            ⚙️ LLM 설정
          </button>
        </div>
      </div>

      <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
      </div>
    </header>
  );
};
