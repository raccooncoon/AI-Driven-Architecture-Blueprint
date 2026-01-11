import { useState, useEffect, useCallback } from 'react';
import { getRequirements, getAllTasks } from '../api';
import type { TaskCard } from '../api';

export const useRequirements = () => {
  const [requirements, setRequirements] = useState<any[]>([]);
  const [taskCards, setTaskCards] = useState<TaskCard[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const [reqData, taskData] = await Promise.all([
        getRequirements(),
        getAllTasks()
      ]);
      setRequirements(Array.isArray(reqData) ? reqData : []);
      setTaskCards(taskData?.success && Array.isArray(taskData.data) ? taskData.data : []);
      setError(null);
    } catch (err: any) {
      console.error('데이터 로딩 실패:', err);
      setError('데이터를 불러오는 데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return {
    requirements,
    setRequirements,
    taskCards,
    setTaskCards,
    loading,
    error,
    setError,
    refetch: fetchData
  };
};
