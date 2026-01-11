import React from 'react';

interface BatchProgressProps {
  current: number;
  total: number;
}

export const BatchProgress: React.FC<BatchProgressProps> = ({ current, total }) => {
  if (total === 0) return null;

  const percentage = Math.round((current / total) * 100);

  return (
    <div style={{
      position: 'fixed',
      bottom: '2rem',
      right: '2rem',
      background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
      padding: '1.5rem',
      borderRadius: '20px',
      boxShadow: '0 12px 40px rgba(0,0,0,0.6), 0 0 0 1px rgba(148, 163, 184, 0.1)',
      zIndex: 2000,
      minWidth: '320px',
      border: '1px solid rgba(59, 130, 246, 0.2)',
      backdropFilter: 'blur(10px)',
      animation: 'slideUp 0.5s cubic-bezier(0.4, 0, 0.2, 1)'
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontSize: '0.8rem', color: '#94a3b8', fontWeight: 600, marginBottom: '0.25rem', textTransform: 'uppercase', letterSpacing: '1px' }}>
            Batch Processing
          </div>
          <div style={{ fontSize: '1.25rem', color: 'white', fontWeight: 800 }}>
            {current} <span style={{ color: '#475569', fontSize: '0.9rem' }}>/</span> {total} <span style={{ fontSize: '0.9rem', color: '#60a5fa', marginLeft: '0.5rem' }}>({percentage}%)</span>
          </div>
        </div>
        <div style={{ fontSize: '1.5rem' }}>⚙️</div>
      </div>
      <div style={{
        width: '100%',
        height: '8px',
        backgroundColor: '#334155',
        borderRadius: '4px',
        overflow: 'hidden',
        boxShadow: 'inset 0 2px 4px rgba(0,0,0,0.3)'
      }}>
        <div style={{
          width: `${percentage}%`,
          height: '100%',
          background: 'linear-gradient(90deg, #3b82f6 0%, #8b5cf6 100%)',
          transition: 'width 0.5s cubic-bezier(0.4, 0, 0.2, 1)',
          boxShadow: '0 0 12px rgba(59, 130, 246, 0.5)'
        }} />
      </div>
    </div>
  );
};
