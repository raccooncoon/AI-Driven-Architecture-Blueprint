import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { getRequirements } from '../api';
import type { Requirement } from './RequirementInput';
import './RequirementList.css';

export const RequirementList: React.FC = () => {
  const { data: requirements, isLoading, isError, error } = useQuery<Requirement[]>({
    queryKey: ['requirements'],
    queryFn: getRequirements,
    staleTime: 0, // Ensure it's always considered stale for this demo
  });

  if (isLoading) return <div className="loading">Loading requirements...</div>;
  if (isError) return <div className="error">Error loading requirements: {error.message}</div>;

  return (
    <div className="list-container">
      <h2>Saved Requirements</h2>
      <div className="requirements-grid">
        {requirements?.map((req) => (
          <div key={req.id} className="requirement-card">
            <div className="card-header">
              <span className="id-tag">#{req.id}</span>
              <span className="category-tag">{req.categoryId}</span>
              <span className={`priority-tag ${req.priority?.toLowerCase()}`}>{req.priority}</span>
            </div>
            <h4>{req.summary}</h4>
            <p className="raw-text-preview">{req.rawText.substring(0, 100)}...</p>
            <div className="card-detail">
              <h5>Technical Detail</h5>
              <p>{req.detail}</p>
            </div>
          </div>
        ))}
        {requirements?.length === 0 && <p className="no-data">No requirements saved yet.</p>}
      </div>
    </div>
  );
};
