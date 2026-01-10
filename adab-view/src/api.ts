import axios from 'axios';

export const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

export const getRequirements = async () => {
    const response = await api.get('/requirements');
    return response.data;
};

export type TaskCard = {
  id: string;
  parentRequirementId: string;
  parentIndex: number;
  summary: string;
  majorCategoryId: string;
  majorCategory: string;
  detailFunctionId: string;
  detailFunction: string;
  subFunction: string;
}

// 과업 존재 여부 확인
export const checkTasksExist = async (requirementId: string): Promise<{ exists: boolean; count: number }> => {
  const response = await api.get(`/tasks/requirement/${requirementId}/exists`);
  return response.data;
};

// 요구사항별 과업 삭제
export const deleteTasksByRequirement = async (requirementId: string): Promise<{ deletedCount: number }> => {
  const response = await api.delete(`/tasks/requirement/${requirementId}`);
  return response.data;
};

// 과업 생성
export const generateTasksWithBackend = async (
  requirement: any,
  onStatus: (message: string) => void,
  onTask: (task: TaskCard) => void,
  onComplete: () => void,
  onError: (error: Error) => void
) => {
  try {
    const response = await fetch('http://localhost:8080/api/tasks/generate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
      },
      body: JSON.stringify(requirement),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const reader = response.body?.getReader();
    if (!reader) {
      throw new Error('응답 스트림을 읽을 수 없습니다');
    }

    const decoder = new TextDecoder();
    let buffer = '';

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      buffer += decoder.decode(value, { stream: true });
      const lines = buffer.split('\n');
      buffer = lines.pop() || '';

      for (const line of lines) {
        if (line.trim() === '') continue;

        if (line.startsWith('event:')) {
          const eventType = line.substring(6).trim();
          continue;
        }

        if (line.startsWith('data:')) {
          const data = line.substring(5).trim();

          try {
            const parsed = JSON.parse(data);

            // status 이벤트
            if (parsed.message && !parsed.id) {
              onStatus(parsed.message);
            }
            // task 이벤트
            else if (parsed.id) {
              onTask(parsed as TaskCard);
            }
          } catch (e) {
            console.error('SSE 파싱 에러:', e);
          }
        }
      }
    }

    onComplete();

  } catch (error) {
    console.error('과업 생성 오류:', error);
    onError(error as Error);
  }
};
