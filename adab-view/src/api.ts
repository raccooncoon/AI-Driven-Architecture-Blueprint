import axios from 'axios';

export const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

export const getRequirements = async () => {
    const response = await api.get('/requirements');
    return response.data;
};

// 전체 과업 조회
export const getAllTasks = async () => {
    const response = await api.get('/tasks/all');
    return response.data;
};

// 요구사항 수정
export const updateRequirement = async (requirementId: string, data: any) => {
    const response = await api.put(`/requirements/${requirementId}`, data);
    return response.data;
};

// RFP 샘플 데이터 일괄 업로드
export const uploadRequirementsBatch = async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await api.post('/requirements/batch', formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
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
  generatedBy?: string;
  createdAt?: string;
  updatedAt?: string;
}

export type ModelConfig = {
  id?: number;
  name: string;
  enabled?: boolean;
  isDefault?: boolean;
  modelName?: string;
  apiKey?: string;
  baseUrl?: string;
  temperature?: string;
  maxTokens?: string;
  createdAt?: string;
  updatedAt?: string;
}

// 모델 설정 조회
export const getModelConfigs = async (): Promise<ModelConfig[]> => {
  const response = await api.get('/model-configs');
  return response.data;
};

// 모델 설정 업데이트
export const updateModelConfig = async (name: string, data: Partial<ModelConfig>) => {
  const response = await api.put(`/model-configs/${name}`, data);
  return response.data;
};

// 기본 모델 설정 조회
export const getDefaultModelConfig = async (): Promise<ModelConfig> => {
  const response = await api.get('/model-configs/default');
  return response.data;
};

// 현재 사용 중인 모델 이름 조회
export const getCurrentModelName = async (): Promise<string> => {
  const response = await api.get('/model-configs/current');
  return response.data.modelName;
};

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
    let currentEventType = '';

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      buffer += decoder.decode(value, { stream: true });
      const lines = buffer.split('\n');
      buffer = lines.pop() || '';

      for (const line of lines) {
        if (line.trim() === '') continue;

        if (line.startsWith('event:')) {
          currentEventType = line.substring(6).trim();
          continue;
        }

        if (line.startsWith('data:')) {
          const data = line.substring(5).trim();

          try {
            const parsed = JSON.parse(data);

            // status 이벤트
            if (currentEventType === 'status' || (parsed.message && !parsed.id)) {
              onStatus(parsed.message);
            }
            // task 이벤트
            else if (currentEventType === 'task' || parsed.id) {
              onTask(parsed as TaskCard);
            }
            // complete 이벤트
            else if (currentEventType === 'complete') {
              onStatus(parsed.message);
            }
            // error 이벤트
            else if (currentEventType === 'error') {
              throw new Error(parsed.message || '과업 생성 중 오류 발생');
            }
          } catch (e: any) {
            if (e.message && e.message !== 'SSE 파싱 에러') {
              throw e;
            }
            console.error('SSE 파싱 에러:', e);
          }

          currentEventType = '';
        }
      }
    }

    onComplete();

  } catch (error) {
    console.error('과업 생성 오류:', error);
    onError(error as Error);
  }
};
