import React, { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../api'; // Adjust path if necessary depending on location
import './RequirementInput.css';

export interface Requirement {
  id: number;
  rawText: string;
  categoryId: string;
  summary: string;
  detail: string;
  priority: string;
}

export const RequirementInput: React.FC = () => {
  const [input, setInput] = useState('');
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: async (text: string) => {
      const response = await api.post<Requirement>('/requirements/analyze', text, {
          headers: {
              'Content-Type': 'text/plain'
          }
      });
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['requirements'] });
    },
  });

  const handleSubmit = () => {
    if (input.trim()) {
      mutation.mutate(input);
    }
  };

  return (
    <div className="input-container">
      <h2>New Requirement Analysis</h2>
      
      <textarea
        className="input-area"
        value={input}
        onChange={(e) => setInput(e.target.value)}
        placeholder="Enter your requirement here (e.g. 'The system shall support user login via Google OAuth')..."
        rows={4}
      />

      <button 
        className="analyze-btn"
        onClick={handleSubmit}
        disabled={mutation.isPending || !input.trim()}
      >
        {mutation.isPending ? 'Analyzing...' : 'Analyze Requirement'}
      </button>

      {mutation.isError && (
        <div style={{ color: '#ef4444', marginTop: '1rem' }}>
          Error analyzing requirement: {mutation.error.message}
        </div>
      )}

      {mutation.data && (
        <div className="result-card">
          <div className="result-header">
            <h3>Analysis Result</h3>
            <span className="category-tag">{mutation.data.categoryId}</span>
          </div>
          
          <div className="result-section">
            <h4>Summary</h4>
            <p>{mutation.data.summary}</p>
          </div>

          <div className="result-section">
            <h4>Technical Detail</h4>
            <p>{mutation.data.detail}</p>
          </div>
        </div>
      )}
    </div>
  );
};
