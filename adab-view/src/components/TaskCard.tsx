import React from 'react';
import type { TaskCard as TaskCardType } from '../api';
import { TypewriterText } from './TypewriterText';

interface TaskCardProps {
  task: TaskCardType;
}

export const TaskCard: React.FC<TaskCardProps> = ({ task }) => {
  return (
    <div
      style={{
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
        border: '1px solid rgba(51, 65, 85, 0.5)',
        borderRadius: '16px',
        padding: '1.75rem',
        boxShadow: '0 8px 24px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(59, 130, 246, 0.1)',
        transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        display: 'flex',
        flexDirection: 'column',
        gap: '1.25rem',
        position: 'relative',
        overflow: 'hidden',
        animation: 'fadeInUp 0.5s ease-out'
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = 'translateY(-4px)';
        e.currentTarget.style.boxShadow = '0 12px 32px rgba(0, 0, 0, 0.6), 0 0 0 1px rgba(59, 130, 246, 0.3)';
        e.currentTarget.style.borderColor = 'rgba(59, 130, 246, 0.4)';
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = 'translateY(0)';
        e.currentTarget.style.boxShadow = '0 8px 24px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(59, 130, 246, 0.1)';
        e.currentTarget.style.borderColor = 'rgba(51, 65, 85, 0.5)';
      }}
    >
      <div style={{
        position: 'absolute',
        top: 0,
        left: 0,
        width: '4px',
        height: '100%',
        background: 'linear-gradient(180deg, #3b82f6 0%, #8b5cf6 100%)'
      }} />

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.8rem', flexWrap: 'wrap', gap: '0.5rem' }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.75rem',
          flexWrap: 'wrap'
        }}>
          <span style={{
            fontSize: '0.85rem',
            color: '#60a5fa',
            fontWeight: 800,
            background: 'rgba(59, 130, 246, 0.15)',
            padding: '0.3rem 0.6rem',
            borderRadius: '6px',
            border: '1px solid rgba(59, 130, 246, 0.2)',
            fontFamily: 'monospace'
          }}>
            ID: {task.majorCategoryId}
          </span>
          <div style={{ display: 'flex', gap: '0.6rem', flexWrap: 'wrap' }}>
            <span style={{
              padding: '0.35rem 0.85rem',
              background: 'rgba(59, 130, 246, 0.12)',
              color: '#60a5fa',
              borderRadius: '10px',
              fontSize: '0.75rem',
              fontWeight: 800,
              border: '1px solid rgba(59, 130, 246, 0.25)',
              boxShadow: '0 2px 10px rgba(59, 130, 246, 0.15)',
              letterSpacing: '0.3px',
              textShadow: '0 0 10px rgba(59, 130, 246, 0.3)'
            }}>
              {task.majorCategory}
            </span>
            <span style={{
              padding: '0.35rem 0.85rem',
              background: 'rgba(168, 85, 247, 0.1)',
              color: '#c084fc',
              borderRadius: '10px',
              fontSize: '0.75rem',
              fontWeight: 800,
              border: '1px solid rgba(168, 85, 247, 0.25)',
              boxShadow: '0 2px 10px rgba(168, 85, 247, 0.1)',
              letterSpacing: '0.2px',
              textShadow: '0 0 8px rgba(168, 85, 247, 0.2)'
            }}>
              {task.detailFunction}
            </span>
          </div>
        </div>
      </div>

      <div>
        <div style={{
          fontSize: '0.8rem',
          color: '#94a3b8',
          fontWeight: 600,
          marginBottom: '0.5rem',
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem'
        }}>
          <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: '#8b5cf6' }} />
          세부 기능
        </div>
        <div style={{
          fontSize: '1.1rem',
          color: '#f8fafc',
          fontWeight: 700,
          lineHeight: '1.4'
        }}>
          {task.isNew ? (
            <TypewriterText text={task.subFunction} speed={15} />
          ) : task.subFunction}
        </div>
      </div>

      <div style={{
        background: 'rgba(15, 23, 42, 0.5)',
        padding: '1.25rem',
        borderRadius: '12px',
        border: '1px solid rgba(148, 163, 184, 0.05)'
      }}>
        <div style={{
          fontSize: '0.8rem',
          color: '#60a5fa',
          fontWeight: 600,
          marginBottom: '0.75rem',
          textTransform: 'uppercase',
          letterSpacing: '0.5px'
        }}>
          과업 요약
        </div>
        <div style={{
          fontSize: '0.95rem',
          color: '#cbd5e1',
          lineHeight: '1.7',
          whiteSpace: 'pre-wrap'
        }}>
          {task.isNew ? (
            <TypewriterText text={task.summary} speed={10} delay={500} />
          ) : task.summary}
        </div>
      </div>

      {/* Footer Meta Info */}
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginTop: '0.5rem',
        paddingTop: '0.75rem',
        borderTop: '1px solid rgba(148, 163, 184, 0.1)',
        fontSize: '0.75rem',
        color: '#64748b'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <span style={{ 
            padding: '2px 6px', 
            background: 'rgba(59, 130, 246, 0.1)', 
            borderRadius: '4px', 
            color: '#3b82f6',
            fontWeight: 700
          }}>
            LLM Model
          </span>
          <span>{task.generatedBy || 'ADAB System'}</span>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
          <span>
            {task.createdAt 
              ? new Date(task.createdAt as string).toLocaleString('ko-KR', {
                  year: 'numeric', month: '2-digit', day: '2-digit',
                  hour: '2-digit', minute: '2-digit', second: '2-digit'
                }) 
              : '시간 정보 없음'
            }
          </span>
        </div>
      </div>
    </div>
  );
};
