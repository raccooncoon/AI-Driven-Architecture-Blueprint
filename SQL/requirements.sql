INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.844109', '2026-01-11 03:12:15.844146', '2026-01-31', '생성형 AI 모델 개념 정의', e'ㅇ PO 제공 가능
- 믿음 2.0', '데이터허브 – 생성형 AI 모델', 'BA: PO/Lab 확인 필요', '○ 프라이빗 클라우드 환경에서 활용 가능한 LLM 모델 제시', 'REQ-AI-BA-0001', 'SFR-DHUB-008', e'제공가능 모델 (이행/PO정의 시)
-믿음 K pro, 라마 K');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.851437', '2026-01-11 03:12:15.851445', '2026-01-31', '생성형 AI 기본사항 개념 정의', e'ㅇ PO 제공 불가
- 멀티모달 입력 불가능(텍스트 입력만 가능)
(PO) 사업 기간 내 제공 불가능 > 별도 SI개발비 필요
ㅇ PO 제공 불가
- 멀티모달 출력(텍스트 출력만 가능)
(PO) 사업 기간 내 제공 불가능 > 별도 SI개발비 필요
- 모델은 오픈소스와 글로벌 상용 API 활용 예정으로 이미지 등에 대한 출력 인터페이스 제공 가능 여부 확인 필요(PO)
(BD) 제안서 작성 시에는 개발 예정 로드맵 포함 상태', '데이터허브 – 생성형 AI 기본사항', 'BA : 멀티모달 입/출력 기능 제공 불가', '○ 생성형 AI 프롬프트 전달을 통해 요구 글 또는 이미지 생성', 'REQ-AI-BA-0003', 'SFR-DHUB-009', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.852075', '2026-01-11 03:12:15.852082', '2026-01-31', '생성형 AI 기본사항 개념 정의', 'ㅇ PO 제공 가능', '데이터허브 – 생성형 AI 기본사항', 'BA: PO/Lab 확인 필요', '○ 필요시 한 번에 16,000자 이상의 텍스트 입력(Input Context Length)및 4,000자 이상 텍스트 생성이 가능할 것', 'REQ-AI-BA-0004', 'SFR-DHUB-009', e'-믿음 K Pro : 32K
-라마 K : 32k');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.852737', '2026-01-11 03:12:15.852745', '2026-01-31', '생성형 AI 기본사항 개념 정의', 'ㅇ PO 제공 가능', '데이터허브 – 생성형 AI 기본사항', 'BA: PO/Lab 확인 필요', '○ 도입되는 하드웨어의 성능을 충분히 활용할 수 있는 수준의 생성형 AI 모델 파라미터 제안할 것 [동시 질의응답 가능 수 : 100건 이상]', 'REQ-AI-BA-0005', 'SFR-DHUB-009', e'전달드린 AI모델/솔루션 성능 및 Spec 자료참고
-믿음 K Pro : H100 80G 2장
(balanced 동접(240), RPS(22.59))
-라마 K 74B : H100 80G 4장
(balanced동접(224), RPS(10.38))');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.853720', '2026-01-11 03:12:15.853729', '2026-01-31', '생성형 AI 기본사항 개념 정의', e'1. K Studio 라이센스 제공
2. SI 개발 사항 소스 제공 가능', '데이터허브 – 생성형 AI 기본사항', 'BA: PO/Lab 확인 필요', '○ LLMOps, RAG, 프롬프트 서비스, 타 시스템을 위한 API 등 생성형 AI 서비스를 위한 모든 소프트웨어 라이선스를 포함', 'REQ-AI-BA-0006', 'SFR-DHUB-009', 'K-Studio 패키징 확인 필요');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.854344', '2026-01-11 03:12:15.854351', '2026-01-31', '생성형 AI 기본사항 개념 정의', 'ㅇ PO 제공 가능', '데이터허브 – 생성형 AI 기본사항', 'BA: 홈 > 플레이그라운드 메뉴 제공', '○ 프롬프트를 작성하여 생성형 AI에 전달할 수 있는 기본 UI 제공', 'REQ-AI-BA-0007', 'SFR-DHUB-009', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.854895', '2026-01-11 03:12:15.854902', '2026-01-31', '생성형 AI 기본사항 개념 정의', 'ㅇ PO 제공 가능', '데이터허브 – 생성형 AI 기본사항', 'BA: On-Prem으로 구축 가능', '○ 온프레미스(On-Premise) 구축이 가능한 언어모델로서 내부망에 설치·운용이 가능해야 함', 'REQ-AI-BA-0008', 'SFR-DHUB-009', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.855357', '2026-01-11 03:12:15.855364', '2026-01-31', '생성형 AI 기본사항 개념 정의', 'ㅇ PO 제공 가능', '데이터허브 – 생성형 AI 기본사항', 'BA: PO/Lab 확인 필요', '○ 기본모델은 한국어 데이터넷으로 사전학습이 완료되어, 한국어 문법, 의미 이해 및 답변이 가능해야 함', 'REQ-AI-BA-0009', 'SFR-DHUB-009', '믿음 K Pro, 라마 K 지원');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.855857', '2026-01-11 03:12:15.855863', '2026-01-31', '생성형 AI 기본사항 개념 정의', e'ㅇ PO 제공 가능
- 유해콘텐츠 차단 기능', '데이터허브 – 생성형 AI 기본사항', 'BA - 모델학습 > 학습데이터 정제 > 텍스트 정제, Toxic Sentence 검출, 개인정보 필터링, De-duplication - 모델학습 > 콘텐츠 필터 - 플레이그라운드 > input 개인정보 필터, output 개인정보 필터, 유해정보 필터', e'○ 콘텐츠 필터링
- 유해 콘텐츠, 개인정보 관련 내용, 윤리원칙에 위배되는 관련 내용 등의 필터링 기능이 포함되어야 함', 'REQ-AI-BA-0010', 'SFR-DHUB-009', e'K-Studio 2.0에 적용된 콘텐츠 필터링 제공
(개인정보, 유해정보 차단, 가드레일)');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.856477', '2026-01-11 03:12:15.856484', '2026-01-31', '생성형 AI 기본사항 개념 정의', e'ㅇ PO 제공 가능
- SFT, PEFT, 강화학습 등', '데이터허브 – 생성형 AI 기본사항', 'BA: SFT, PEFT, 강화학습 제공', e'○ 모델 미세조정
- 서비스 별로 LLM 모델 미세조정이 가능해야 함
- 미세조정을 위한 UI 기반 학습설정이 가능해야하며, 관리도구를 통해 제공해야 함
- 모델 조정은 Full 파인튜닝, PEFT 방식 등 모두 가능해야 함
- 학습된 모델은 망각현상을 최소할 수 있도록 구성되어야 함', 'REQ-AI-BA-0011', 'SFR-DHUB-009', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.856939', '2026-01-11 03:12:15.856946', '2026-01-31', '생성형 AI 기본사항 개념 정의', e'ㅇ PO 제공 가능
- 모델 관리: K Studio 형상 관리 기능 제공', '데이터허브 – 생성형 AI 기본사항', e'BA: K Studio 모델 관리 기능 제공
2.0+ 고도화로 모델 관리기능 개발 예정', e'○ 생성형 AI 모델 통합관리
- K-water내에서 서비스 중인 생성형 AI 모델을 통합관리할 수 있는 기능을 제공해야 하며, 통합서비스를 제공할 수 있는 방안을 제시해야 함', 'REQ-AI-BA-0012', 'SFR-DHUB-009', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.857411', '2026-01-11 03:12:15.857418', '2026-01-31', '생성형 AI 기본사항 개념 정의', 'ㅇ 과업내용 상세 정의 필요, 요구에 따라 AI파트에서 SI개발할수 있는 부분은 아이엠그루와 협의 필요, 기본 개발 요건은 ST로직에서 개발요건임', '데이터허브 – 생성형 AI 기본사항', 'BA: 제공하지 않음', e'○ 생성형 AI를 활용함에 있어 K-watrer의 수자원, 수도, 발전 등 기술분야에서 활용할 수 있는 데이터 분석모델의 결과도 병행 활용할 수 있도록 구축하여야 한다.
- 데이터 분석모델은 K-water 기술분야 업무 프로세스를 검토하여 가장 많이 활용하거나 새로운 인사이트를 줄 수 있는 대상을 도출하여 구축하여야 한다.', 'REQ-AI-BA-0013', 'SFR-DHUB-009', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.857723', '2026-01-11 03:12:15.857730', '2026-01-31', '생성형 AI 기본사항 개념 정의', e'ㅇ PO 제공 가능
- 오픈 소스 모델 지원(학습/서빙) = 오픈소스 최신 모델 받아서, 재배포를 통해 교체', '데이터허브 – 생성형 AI 기본사항', 'BA: 오픈 소스 모델 지원(학습/서빙) = 오픈소스 최신 모델 받아서, 재배포를 통해 교체', '○ 생성형 AI모델은 최신형 모델에 대한 업데이트 및 패치, 교체 등이 원활하게 이뤄져야 하며, 모델 간 비교검증 또한 본 클라우드 내에서 진행될 수 있도록 구축하여야 한다.', 'REQ-AI-BA-0014', 'SFR-DHUB-009', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.858007', '2026-01-11 03:12:15.858013', '2026-01-31', '생성형 AI 기본사항 개념 정의', e'ㅇ PO 부분 제공 가능
- SFT, PEFT, 강화학습 등
ㅇ K Studio 파인튜닝 관리 기능 베이스
- 고객사 구체적 요구사항 확인 작업 필요
- 기존 K water에 도입된 모델 무엇인지 확인 필요', '데이터허브 – 생성형 AI 기본사항', e'BA: SFT, PEFT, 강화학습 제공
홈 > 모델 학습', '○ 파인 튜닝을 관리자가 쉽게 할 수 있도록 하여야 하며, K-water에 기존에 도입된 모델을 검토 후 감독원과 협의하여 K-water 디지털플랫폼에서 종합서비스 될 수 있는 방안 결정 및 구축하여야 한다.', 'REQ-AI-BA-0015', 'SFR-DHUB-009', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.859762', '2026-01-11 03:12:15.859769', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', 'ㅇ PO 제공 가능', '데이터허브 – 생성형 AI 관리도구', 'BA - 학습모델 설정: 관리자페이지 모델 등록, 사용자 페이지 모델 학습 메뉴 제공 - 모델 관리 및 배포: 모델학습 > 배포 > 배포관리 - 프롬프트 관리: 프롬프트 - API 관리: 관리자페이지 API 키 관리, 슈퍼관리자 페이지 API 관리', e'○ 생성형 AI를 관리하기 위한 기본 기능 제공
- 학습모델 기본 설정 : 학습모델 등록, 사전학습 모델 및 데이터 설정
- 학습모델 세부 설정 : 하이퍼파라미터 설정, 데이터 전처리 등 세부설정
- 모델 관리 및 배포 : 모델에 대한 학습, 평가, 감시, 통제, 배포 등
- 프롬프트 관리 : 프롬프트 설계 및 테스트 지원 등
- API 관리 : API 생성, 사용승인 및 연계 프로그램 현황 확인 등', 'REQ-AI-BA-0016', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.860227', '2026-01-11 03:12:15.860234', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', e'ㅇ PO 제공 가능
- 데이터 업/다운로드 제공
- LLM 학습 데이터 정제(텍스트 정제, Toxic Sentence, 개인정보 필터링, De-duplication) 기능 제공 중', '데이터허브 – 생성형 AI 관리도구', e'BA
- 학습 데이터 업/다운로드 제공
- LLM 학습 데이터 정제(텍스트 정제, Toxic Sentence, 개인정보 필터링, De-duplication) 기능 제공 중', e'○ 학습데이터 관리
- 데이터 업/다운로드, 정제, 미세조정 등', 'REQ-AI-BA-0017', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.860701', '2026-01-11 03:12:15.860708', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', 'ㅇ PO 제공 가능', '데이터허브 – 생성형 AI 관리도구', 'BA - 모델학습/배포: 모델 학습 메뉴 - 평가: 평가 메뉴', e'○ 모델 학습, 평가, 배포 기능을 제공해야 함
- 사용자가 직관적으로 관리할 수 있도록 UI 제공', 'REQ-AI-BA-0018', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.861128', '2026-01-11 03:12:15.861135', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', e'# 사용자가 입력한 프롬프트를 최적화 및 생성하는 기능 제공
ㅇ PO 제공 가능
- 2.0+ AI 프롬프트 고도화 시 지원 예정', '데이터허브 – 생성형 AI 관리도구', 'BA - 2.0+ AI 프롬프트 고도화 시 지원 예정', '○ 프롬프트 생성, 파라미터 생성 및 테스트 기능 제공', 'REQ-AI-BA-0019', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.861505', '2026-01-11 03:12:15.861512', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', e'# LLM모델을 배포 후 입력에 대한 결과를 볼 수 있는 기능 제공
ㅇ PO 제공 가능
- 플레이그라운드', '데이터허브 – 생성형 AI 관리도구', 'BA: 플레이그라운드 메뉴 제공', '○ 모델 추론 테스트 기능 제공', 'REQ-AI-BA-0020', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.861822', '2026-01-11 03:12:15.861830', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', 'ㅇ PO 제공 가능', '데이터허브 – 생성형 AI 관리도구', 'BA - 2.0+ 오픈소스모델 학습 고도화 시 지원 예정', '○ 타 오픈소스 모델 학습 지원 기능 제공', 'REQ-AI-BA-0021', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.862092', '2026-01-11 03:12:15.862100', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', e'ㅇ SSO 연동 필요(사용자 권한/접근 권한 등)
ㅇ 사용자관리 기능은 제품 기능 활용하되 SSO 연동은 SI개발(이행)', '데이터허브 – 생성형 AI 관리도구', 'BA - 관리자페이지 > 사용자 관리 메뉴 제공', '○ 사용자 관리 기능 제공', 'REQ-AI-BA-0022', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.862368', '2026-01-11 03:12:15.862375', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', e'ㅇ (이행) 래블업 기능제공 및 견적 확인 완료
- 수지분석: 0.78억 / 견적금액: 2.05억
(BD) 구두 견적으로 부정확, 네고/협상 필요, 영업 검토 진행', '데이터허브 – 생성형 AI 관리도구', '-', e'○ HW 자원관리 기능
- HW, 메모리, GPU 등', 'REQ-AI-BA-0023', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.862667', '2026-01-11 03:12:15.862674', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', e'ㅇ PO 제공 가능
- 사용자 가이드 제공
- 사용자/관리자 두 가지 권한 관리 기능 제공(관리자의 경우 관리페이지 접근 가능하며, 접속기록/행동 기록 로그 적재 중)
- 모니터링 화면 필요(접속기록, 행동 기록)
- 사용자에게 안내하는 편의 기능 제공
- 서비스 별 접근권한 관리 기능', '데이터허브 – 생성형 AI 관리도구', e'BA
- 사용자 가이드 제공
- 사용자/관리자 두 가지 권한 관리 기능 제공(관리자의 경우 관리페이지 접근 가능하며, 접속기록/행동 기록 로그 적재 중)
- 관리자페이지 > 데이터 관리 > 사용자 데이터 관리(로그인 기록 관리)', e'○ 쉬운 생성형 AI 관리 프로그램 사용
- 사용 매뉴얼 제공
- 사용자를 안내할 수 있는 편의 기능 제공
- 생성형 AI 관리 프로그램 접근권한 관리 및 사용기록 로그 관리(접속기록과 행동 기록 간 분리)', 'REQ-AI-BA-0024', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.863009', '2026-01-11 03:12:15.863016', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', e'ㅇ PO 제공 가능
- (PO) \'최신성\' 명확화 필요, 사업 기간 내 2번의 업데이트 보장
- (이행) 추가 비용 투입, 마이너 업데이트 지원 수준 고려, 사업 기간 내 업데이트 요건으로 발주처 협의 진행
- (BD) LLM 대상 요건임(LLMOps X)', '데이터허브 – 생성형 AI 관리도구', '-', e'○ 최신성 보장
- 관리 프로그램의 기술 최신성을 구축 완료 후 1년간 보장 필요', 'REQ-AI-BA-0025', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.863343', '2026-01-11 03:12:15.863350', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', 'ㅇ PO 제공 가능', '데이터허브 – 생성형 AI 관리도구', 'BA - 모델학습 > 학습데이터 정제 > 텍스트 정제, Toxic Sentence 검출, 개인정보 필터링, De-duplication - 모델학습 > 콘텐츠 필터 - 플레이그라운드 > input 개인정보 필터, output 개인정보 필터, 유해정보 필터 - K RAG : 키워드/개인정보/유해정보 필터 기능 제공(RAG에서 연결한 LLM모델에 적용된 필터링이 적용됨)', e'○ 개인정보 마스킹 및 필터링
- 개인정보가 포함된 문서나 데이터를 활용할 경우 공사에서 제공하는 솔루션이나 자체 도구를 활용하여 마스킹처리하여 제공해야 함', 'REQ-AI-BA-0026', 'SFR-DHUB-010', e'K-Studio 2.0에 적용된 콘텐츠 필터링 제공
(개인정보, 유해정보 차단, 가드레일)');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.863673', '2026-01-11 03:12:15.863679', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', 'ㅇ 이행에서 진행', '데이터허브 – 생성형 AI 관리도구', '-', e'○ DRM
- 문서의 경우 DRM으로 암호화되어 있어서 활용 시 공사 DRM 솔루션을 활용하여 문서 학습, 요약을 제공해야 함
* 공사 DRM 솔루션 – Fasoo DRM', 'REQ-AI-BA-0027', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.864036', '2026-01-11 03:12:15.864043', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', e'ㅇ K Studio 내 기능 필수 포함 필요
(BA) 추가 확인 필요, 답변 내용 다운로드 기능 부재/과업 미고려 사항
(이행) 제공 기한 확정(고객 요구 고려), 라이선스비 고려', '데이터허브 – 생성형 AI 관리도구', 'BA - 문서를 요약하는 기능 없음 - 플레이그라운드에서 생성한 결과를 다운로드 받는 기능 없음', e'○ 문서 생성 포맷
- 프롬프트로 작성한 문서를 요약하거나 생성한 결과를 다운로드할 경우 hwpx, ppt, excel, csv, pdf 형식으로 제공해야 함', 'REQ-AI-BA-0028', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.864459', '2026-01-11 03:12:15.864468', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', e'ㅇ PO 제공 가능
- 모델 관리: K Studio 형상 관리 기능 제공
- 기도입된 LLM모델을 직접 Serving하거나 생성형 AI API를 제공받아 통합관리 하는 기능', '데이터허브 – 생성형 AI 관리도구', 'BA: Studio 를 통해 배포된 모델만 관리 가능함', e'○ 다른 생성형 AI 모델 관리 기능
- 해당 생성형 AI 모델 이외에 K-water에 기도입된 생성형 AI를 통합관리할 수 있는 기능을 제공해야 함', 'REQ-AI-BA-0029', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.864777', '2026-01-11 03:12:15.864783', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', '`=- 향후 운영/유지보수 시점에 발주처가 업그레이드 가능하도록 하는 기능 있는지, 비용은 어떻게 되는지 확인 필요 (이행) 추후 발주처 입장 고려 필요 (PO) 일부 가이드 제공 가능', '데이터허브 – 생성형 AI 관리도구', '-', '○ 생성형 AI 관리도구의 유지관리 및 기능개선을 위하여 자체적으로 업그레이드를 수행할 수 있도록 제공해야 함', 'REQ-AI-BA-0030', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.865058', '2026-01-11 03:12:15.865064', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', e'ㅇ 사업개발/이행
- 출구전략 마련 예정', '데이터허브 – 생성형 AI 관리도구', '-', '○ 생성형 AI관리 도구의 기능 등에 대해 다양한 제품중 실증 대상 SW를 감독원과 협의 후 결정하여 실증 및 실증결과를 제출 후 구축하여야 한다.', 'REQ-AI-BA-0031', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.865399', '2026-01-11 03:12:15.865405', '2026-01-31', '생성형 AI를 관리하는 전용 프로그램 정의', e'ㅇ API 연계 방식등 설계를 아이엠그루에서 하는 방안 외에 대안이 있는지 확인 필요
(K Studio 기본 기능 외에는 SI개발)', '데이터허브 – 생성형 AI 관리도구', '-', '○ 「공공부분 초거대 AI 도입·활용 가이드라인」에 따라 정부에서 추진중인 ‘정부 전용 초거대 AI 인프라’를 연계·활용하는 방안을 제시해야한다.', 'REQ-AI-BA-0032', 'SFR-DHUB-010', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.865893', '2026-01-11 03:12:15.865912', '2026-01-31', '문서 업로드 후 질의응답', e'ㅇ PO 제공 가능
- K Studio RAG', '데이터허브 – 문서 업로드 후 질의응답', e'BA
- RAG 메뉴를 통해 문서 업로드하고 업로드된 문서에 대해 질의응답 가능
- 문서 요약은 불가', e'○ 문서를 업로드하여 업로드된 문서에 대한 질의응답이 가능할 것
(예시)
- 문서를 업로드하여 “이 문서를 A4 한 장으로 요약해줘”
- 문서를 업로드하여 “이 문서를 첨부한 문서의 형태로 요약해줘”', 'REQ-AI-BA-0033', 'SFR-DHUB-011', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.866204', '2026-01-11 03:12:15.866210', '2026-01-31', '문서 업로드 후 질의응답', 'ㅇ K Studio 내 기능 필수 포함 필요', '데이터허브 – 문서 업로드 후 질의응답', e'BA
- RAG 메뉴를 통해 문서 업로드하고 업로드된 문서에 대해 질의응답 가능
- 문서 요약은 불가', '○ 한글(hwpx), 워드(docx) 로 된 문서를 요약할 수 있어야 하고, 한글(hwpx), 워드(docx)로 제공이 가능해야 함', 'REQ-AI-BA-0034', 'SFR-DHUB-011', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.866474', '2026-01-11 03:12:15.866480', '2026-01-31', '문서 업로드 후 질의응답', e'ㅇ PO 제공 가능
- K Studio RAG', '데이터허브 – 문서 업로드 후 질의응답', 'ktds 확인(문서업데이트)', '○ 해당 기능을 API로 제공할 것', 'REQ-AI-BA-0035', 'SFR-DHUB-011', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.866768', '2026-01-11 03:12:15.866774', '2026-01-31', '데이터허브에 저장된 수도, 수자원 데이터를 검색하고 통계 서비스를 제공', e'ㅇ SI개발(이행) 이후 KT DS에서 인수인계 받아 K Intelligence Studio 내 적용, Agent Builder를 활용하여 Agent 구성, 배포, 활용, 추후 KT DS 기능 유지보수
(PO) 시범서비스의 형상 모르는 상황 등
(이행) 본사업 내 가능 여부 > (PO) 성능이 보장된다면 Integration 가능
ㅇ 기본 기능은 제공(단순 질의) 이를 해결하기 위한 제안내용 확인 후 대응(TAG, 온톨로지, GraphDB등)
> (BD) 이행, 제타(이행), 스타스키마+그래프(사업개발)', '데이터허브 – 자연어 검색 및 통계 서비스 제공', 'BA - 이행에서 시범서비스로 개발한 T2SQL 기능을 K Studio 내 마이그레이션 계획 없음 - 2.0+ 고도화 항목으로 메타관리 메뉴 제공 예정', e'○ 데이터허브에 수집된 데이터를 바탕으로 자연어 검색 서비스를 제공
(예시)
- “2024년 7월 10일 대청댐의 수질과 수위를 알려줘”
- “2024년 8월 한 달간 대청댐의 수질, 수량, 강우량 정보를 표로 보여줘”
- “2024년 8월 한 달간 대청댐의 수질, 수량, 강우량 정보를 차트로 보여줘”', 'REQ-AI-BA-0036', 'SFR-DHUB-012', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.867154', '2026-01-11 03:12:15.867160', '2026-01-31', '데이터허브에 저장된 수도, 수자원 데이터를 검색하고 통계 서비스를 제공', e'ㅇ SI개발(이행) 이후 KT DS에서 인수인계 받아 K Intelligence Studio 내 적용, Agent Builder를 활용하여 Agent 구성, 배포, 활용, 추후 KT DS 기능 유지보수
ㅇ 에스티로직의 기본 과업 협의 사항외에 AI영역에서 다시 메타정비 벡터화 하는 등 과업이 정리됐는지 확인 필요', '데이터허브 – 자연어 검색 및 통계 서비스 제공', 'BA - 이행에서 시범서비스로 개발한 T2SQL 기능을 K Studio 내 마이그레이션 계획 없음 - 2.0+ 고도화 항목으로 메타관리 메뉴 제공 예정', '○ 데이터허브에 저장된 DB, 문서 등의 데이터를 기반으로 검색 서비스를 제공하여야 하며 이를 위해 업무프로세스 분석을 바탕으로 메타데이터, 데이터 힌트 등을 구축하고 활용하여야 한다.', 'REQ-AI-BA-0037', 'SFR-DHUB-012', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.867473', '2026-01-11 03:12:15.867479', '2026-01-31', '데이터허브에 저장된 수도, 수자원 데이터를 검색하고 통계 서비스를 제공', e'ㅇ SI개발(이행) 이후 KT DS에서 인수인계 받아 K Intelligence Studio 내 적용, Agent Builder를 활용하여 Agent 구성, 배포, 활용, 추후 KT DS 기능 유지보수
- 에스티로직 정비 기반으로 데이터를 가져와서 구현하는 기능은 SI로 해결
ㅇ (PO) 검색 + reranking 파이프라인으로 고정(단일 질의에 대해 한 번의 검색과 한 번의 정렬만 수행), 연쇄형 검색 미지원', '데이터허브 – 자연어 검색 및 통계 서비스 제공', 'BA - 이행에서 시범서비스로 개발한 T2SQL 기능을 K Studio 내 마이그레이션 계획 없음 - 2.0+ 고도화 항목으로 메타관리 메뉴 제공 예정', '○ DB의 경우 검색질의 시 데이터허브에 수집되는 데이터를 기반으로 검색 서비스를 제공해야 함', 'REQ-AI-BA-0038', 'SFR-DHUB-012', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.873397', '2026-01-11 03:12:15.873403', '2026-01-31', 'AI, DT 개발에 필요한 프로그램 코드 생성을 제공', 'ㅇ SI개발(이행)', '데이터허브 – 코드 어시스트 제공', '-', e'○ AI 분석 및 DT 개발에 필요한 SQL 쿼리 생성
(예시)
- “RWIS에서 A테이블과 B테이블을 활용하여 최근 데이터 1000개의 항
목을 조회할 수 있는 쿼리를 생성해줘”
* 쿼리는 ANSI SQL 기준으로 생성', 'REQ-AI-BA-0055', 'SFR-DHUB-014', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.867838', '2026-01-11 03:12:15.867845', '2026-01-31', '데이터허브에 저장된 수도, 수자원 데이터를 검색하고 통계 서비스를 제공', e'ㅇ SI개발(이행) 이후 KT DS에서 인수인계 받아 K Intelligence Studio 내 적용, Agent Builder를 활용하여 Agent 구성, 배포, 활용, 추후 KT DS 기능 유지보수
ㅇ PO 일부 제공 가능
- LLMOps 초기화면에서 대화형 인터페이스 등을 통해 사용자의 입력을 처리, 대화형 UI 제공
- 멀티턴 제공', '데이터허브 – 자연어 검색 및 통계 서비스 제공', 'BA - 이행에서 시범서비스로 개발한 T2SQL 기능을 K Studio 내 마이그레이션 계획 없음 - 2.0+ 고도화 항목으로 메타관리 메뉴 제공 예정', '○ 검색의 정확성을 향상시키기 위하여 대화형으로 서비스를 제공해야 함', 'REQ-AI-BA-0039', 'SFR-DHUB-012', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.868282', '2026-01-11 03:12:15.868289', '2026-01-31', '데이터허브에 저장된 수도, 수자원 데이터를 검색하고 통계 서비스를 제공', '', '데이터허브 – 자연어 검색 및 통계 서비스 제공', 'BA - 이행에서 시범서비스로 개발한 T2SQL 기능을 K Studio 내 마이그레이션 계획 없음 - 2.0+ 고도화 항목으로 메타관리 메뉴 제공 예정', '○ 자연어 검색질의와 결과는 한글과 영문으로 제공할 수 있어야 함', 'REQ-AI-BA-0040', 'SFR-DHUB-012', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.868620', '2026-01-11 03:12:15.868626', '2026-01-31', '데이터허브에 저장된 수도, 수자원 데이터를 검색하고 통계 서비스를 제공', 'ㅇ SI개발(이행) 이후 KT DS에서 인수인계 받아 K Intelligence Studio 내 적용, Agent Builder를 활용하여 Agent 구성, 배포, 활용, 추후 KT DS 기능 유지보수', '데이터허브 – 자연어 검색 및 통계 서비스 제공', 'BA - 이행에서 시범서비스로 개발한 T2SQL 기능을 K Studio 내 마이그레이션 계획 없음 - 2.0+ 고도화 항목으로 메타관리 메뉴 제공 예정', '○ 웹화면 및 모바일에서 활용 가능한 UI를 제공해야 함', 'REQ-AI-BA-0041', 'SFR-DHUB-012', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.868965', '2026-01-11 03:12:15.868971', '2026-01-31', '데이터허브에 저장된 수도, 수자원 데이터를 검색하고 통계 서비스를 제공', 'ㅇ SI개발(이행) 이후 KT DS에서 인수인계 받아 K Intelligence Studio 내 적용, Agent Builder를 활용하여 Agent 구성, 배포, 활용, 추후 KT DS 기능 유지보수', '데이터허브 – 자연어 검색 및 통계 서비스 제공', 'BA - 이행에서 시범서비스로 개발한 T2SQL 기능을 K Studio 내 마이그레이션 계획 없음 - 2.0+ 고도화 항목으로 메타관리 메뉴 제공 예정', e'○ 필요 시 출처를 답변할 것
- 출처는 문서, DB 등 데이터허브에서 관리하는 데이터 범위임', 'REQ-AI-BA-0042', 'SFR-DHUB-012', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.869296', '2026-01-11 03:12:15.869302', '2026-01-31', '데이터허브에 저장된 수도, 수자원 데이터를 검색하고 통계 서비스를 제공', 'ㅇ SI개발(이행) 이후 KT DS에서 인수인계 받아 K Intelligence Studio 내 적용, Agent Builder를 활용하여 Agent 구성, 배포, 활용, 추후 KT DS 기능 유지보수', '데이터허브 – 자연어 검색 및 통계 서비스 제공', 'BA - 이행에서 시범서비스로 개발한 T2SQL 기능을 K Studio 내 마이그레이션 계획 없음 - 2.0+ 고도화 항목으로 메타관리 메뉴 제공 예정', '○ 할루시네이션 방지할 것(RAG 적용 등)', 'REQ-AI-BA-0043', 'SFR-DHUB-012', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.869700', '2026-01-11 03:12:15.869707', '2026-01-31', '데이터허브에 저장된 수도, 수자원 데이터를 검색하고 통계 서비스를 제공', e'ㅇ (이행) 래블업 기능제공 및 견적 확인 완료
- 수지분석: 0.78억 / 견적금액: 2.05억', '데이터허브 – 자연어 검색 및 통계 서비스 제공', 'BA - 이행에서 시범서비스로 개발한 T2SQL 기능을 K Studio 내 마이그레이션 계획 없음 - 2.0+ 고도화 항목으로 메타관리 메뉴 제공 예정', '○ 해당 기능을 API로 제공할 것', 'REQ-AI-BA-0044', 'SFR-DHUB-012', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.870138', '2026-01-11 03:12:15.870144', '2026-01-31', '생성형', e'ㅇ SI개발(이행)
- 한글 라이선스 해결 방안 확인 필요', '데이터허브 – 분석보고서 생성 서비스', '-', e'○ 특정 문서의 포맷을 지정하고 해당 문서를 학습하고 추론을 통해 문
서 생성
- 보고서 초안 생성 시 문서양식을 참조할 수 있는 기능을 제공
- 문서는 hwpx, ppt, execl, csv 형태로 제공할 수 있어야 함
- 표형식으로 구성된 검색결과는 문서에서 편집할 수 있는 형태로 제공되어야 함', 'REQ-AI-BA-0045', 'SFR-DHUB-013', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.870424', '2026-01-11 03:12:15.870435', '2026-01-31', '생성형', 'ㅇ SI개발(이행)', '데이터허브 – 분석보고서 생성 서비스', '-', '○ 해당 문서의 품질을 향상시킬 수 있도록 파인튜닝이 가능하도록 구축할 것', 'REQ-AI-BA-0046', 'SFR-DHUB-013', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.870680', '2026-01-11 03:12:15.870686', '2026-01-31', '생성형', 'ㅇ SI개발(이행)', '데이터허브 – 분석보고서 생성 서비스', '-', '○ 문서 생성 시 참조한 문헌 정보를 제공해야 함', 'REQ-AI-BA-0047', 'SFR-DHUB-013', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.870972', '2026-01-11 03:12:15.870980', '2026-01-31', '생성형', 'ㅇ SI개발(이행)', '데이터허브 – 분석보고서 생성 서비스', '-', '○ 일반 사용자가 업로드 한 문서는 해당 사용자만 활용해야 하고, 다른 사용자의 답변에 영향을 주지 않아야 함', 'REQ-AI-BA-0048', 'SFR-DHUB-013', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.871348', '2026-01-11 03:12:15.871354', '2026-01-31', '생성형', 'ㅇ SI개발(이행)', '데이터허브 – 분석보고서 생성 서비스', '-', '○ 해당 기능은 DevOps 형태로 제공해야 함', 'REQ-AI-BA-0049', 'SFR-DHUB-013', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.871766', '2026-01-11 03:12:15.871771', '2026-01-31', '생성형', 'ㅇ SI개발(이행)', '데이터허브 – 분석보고서 생성 서비스', '-', '○ 추론(응답, 문서 생성)에 대한 정확도(선호도) 평가 기능 제공', 'REQ-AI-BA-0050', 'SFR-DHUB-013', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.872102', '2026-01-11 03:12:15.872107', '2026-01-31', '생성형', 'ㅇ SI개발(이행)', '데이터허브 – 분석보고서 생성 서비스', '-', e'○ 할루시네이션 방지할 것(RAG 적용 등)
- 신뢰성 향상을 위한 기능 제공', 'REQ-AI-BA-0051', 'SFR-DHUB-013', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.872445', '2026-01-11 03:12:15.872451', '2026-01-31', '생성형', 'ㅇ SI개발(이행)', '데이터허브 – 분석보고서 생성 서비스', '-', e'○ 분석보고서 생성 시범 서비스 제공
- K-water에서 협의를 통해 수자원, 수도, 발전 등 기술문서 서식 5종을 감독원과 협의하여 시범서비스를 제공
- 검토대상 문서는 일일수질점검 보고 등 주기성을 갖는 정형화된 문서를 우선 검토하며, 자연어 요청으로 해당 문서를 작성 및 필요시 결재연동 등을 통해 업무에 활용할 수 있도록 하여야 한다.', 'REQ-AI-BA-0052', 'SFR-DHUB-013', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.872764', '2026-01-11 03:12:15.872770', '2026-01-31', 'AI, DT 개발에 필요한 프로그램 코드 생성을 제공', 'ㅇ SI개발(이행)', '데이터허브 – 코드 어시스트 제공', '-', e'○ 데이터허브를 통해 AI, DT 개발을 위해 프로그램(자바, 파이선, R 등)에서 활용 가능한 데이터소스 정보를 제공
(예시)
- “파이선에서 RWIS DB에 접속하기 위한 데이터소스 정보를 알려줘”', 'REQ-AI-BA-0053', 'SFR-DHUB-014', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.873082', '2026-01-11 03:12:15.873088', '2026-01-31', 'AI, DT 개발에 필요한 프로그램 코드 생성을 제공', 'ㅇ SI개발(이행)', '데이터허브 – 코드 어시스트 제공', '-', e'○ AI 분석 및 DT 개발에 필요한 메타데이터 정보를 제시
(예시)
- “RWIS에서 수질항목이 있는 테이블의 논리명과 물리명을 알려줘”
- “RWIS에서 있는 테이블 항목 정보를 알려줘”', 'REQ-AI-BA-0054', 'SFR-DHUB-014', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.873765', '2026-01-11 03:12:15.873771', '2026-01-31', 'AI, DT 개발에 필요한 프로그램 코드 생성을 제공', 'ㅇ SI개발(이행)', '데이터허브 – 코드 어시스트 제공', '-', e'○ 자바, 파이썬 코드 어시스트 기능 제공
(예시) “RWIS의 A테이블을 조회하는 코드를 생성해줘”
(예시) “디지털트윈에 대청댐을 지도를 이동할 수 있는 자바스크립트를 생성해줘 ”', 'REQ-AI-BA-0056', 'SFR-DHUB-014', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.874250', '2026-01-11 03:12:15.874257', '2026-01-31', 'AI, DT 개발에 필요한 프로그램 코드 생성을 제공', 'ㅇ SI개발(이행)', '데이터허브 – 코드 어시스트 제공', '-', e'○ 자바, 파이썬 코드 주석 분석
(예시) “업로드한 자바, 파이썬 코드를 보고 코드 주석을 생성해줘”', 'REQ-AI-BA-0057', 'SFR-DHUB-014', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.874494', '2026-01-11 03:12:15.874501', '2026-01-31', 'AI, DT 개발에 필요한 프로그램 코드 생성을 제공', 'ㅇ SI개발(이행)', '데이터허브 – 코드 어시스트 제공', '-', e'○ 할루시네이션 방지할 것(RAG 적용 등)
- 신뢰성 향상을 위한 기능 제공', 'REQ-AI-BA-0058', 'SFR-DHUB-014', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.874774', '2026-01-11 03:12:15.874779', '2026-01-31', '개인이 질의한 내용을 관리하는 기능 제공', e'ㅇ PO K Studio 개인별 질의 내용 히스토리 관리 기능 제공 필요
(+멀티턴 기능)
- 히스토리: (BA) 제공 중 > (이행) 1,000개 수준 가능 여부 > (BA) 확인 필요
- 멀티턴: Agent LLM Node 제공 예정', '데이터허브 – 개인별 질의 내용 히스토리 관리', e'BA: 플레이그라운드에서 멀티턴 지원함
- 대화 이력 100개 (30일) 관리 중
- 대화 이력에 들어가서 해야함
- 각 대화 이력별 저장기간:', e'○ 데이터허브에 접속시 최근에 질의한 내용을 제공하는 기능 제공
- 최소 한달분(1,000개 질의) 질의내용 저장 관리
○ 질의를 수행한 유저의 질의 정보를 기억하고 새로운 질의에 이어 응답할 것
(예시)
- “일주일 전에 질의한 내용을 알려줘”
- “한 달 전에 질의한 내용에 대한 답변을 요약해 줘”
- “어제 마지막 질의한 내용에 대해 더욱 상세하게 답변해 줘”', 'REQ-AI-BA-0059', 'SFR-DHUB-015', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.875121', '2026-01-11 03:12:15.875126', '2026-01-31', '데이터허브를 기반으로 데이터를 전환하고 기존의 Legacy 시스템을 데이터소스를 변경', e'ㅇ PO 제공 가능
- 모델 학습 현황(로스, 학습률, 평가 데이터에 대한 배치 성능, 메트릭): 오픈소스모델 학습(RLHF포함) 기능으로 2.0+ 고도화 완료 시 제공 가능
- 모델 활용 현황(대시보드): 관리자 페이지에서 기능 제공 중', '데이터허브 – 관리자 기능', 'BA - 모델 학습 현황: 모델학습 > 학습된 모델 상세 - 모델 활용 현황: 슈퍼관리자 페이지 > 모델 배포 현황, 사용자/관리자페이지 > 배포 관리', '○ 생성형 AI 활용 현황 및 추론, 학습 결과 등을 모니터링할 수 있는 기능 제공', 'REQ-AI-BA-0060', 'SFR-DHUB-017', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.875435', '2026-01-11 03:12:15.875440', '2026-01-31', '성능 일반 요구사항 목적 정의', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '성능 일반요건', '-', e'○ 성능 일반 사항
- 대부분의 서비스 기능은 실시간 처리가 가능하도록 사용자가 요청한 때로부터 감독원과 협의한 시간 이내에 결과값을 보여주어야 하며, 성능 테스트 시 초과될 경우 원인을 분석하고 해결방안을 제시해야 함
- 기타 타당한 사유가 있어 감독원과 협의한 시간 이내의 응답시간을 초과할 경우에는 발주기관과 협의 결정하여야 함
- 이 요구사항은 임의의 선택 기준이 허용되는 대량의 데이터에 대한 통계 및 데이터 검색에는 적용되지 않음
- 이 요구사항은 시스템을 사용하는 사용자 숫자가 동시 사용자 용량의 90%를 초과하는 경우에는 적용되지 않음', 'REQ-AI-BA-0061', 'PER-001', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.875768', '2026-01-11 03:12:15.875774', '2026-01-31', '성능 일반 요구사항 목적 정의', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '성능 일반요건', '-', e'○ 사업대상 시스템의 성능을 고려한 개발 방안 제시
- 목표 시스템에 대하여 안정적 운영지원 및 사용자 지원 방안을 제시하고, 향후 확장성, 호환성, 유연성 등을 충분히 고려한 개발 방안을 제시
- 시스템 개발 중 로그 또는 툴(도구)을 이용하여 시스템 성능상태를 모니터링하며 문제를 미리 파악하여 조치한 후 시스템 오픈', 'REQ-AI-BA-0062', 'PER-001', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.876106', '2026-01-11 03:12:15.876111', '2026-01-31', '성능 일반 요구사항 목적 정의', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '성능 일반요건', '-', e'○ 무중단 업그레이드 패치
- 디지털플랫폼 IaaS, PaaS, SaaS 운영을 위한 제품 업그레이드, 패치가 필요할 경우 무중단 상태에서 가능하도록 방안을 제시해야함
- 제품 업그레이드, 패치 이후 기 구축한 시스템, 분석모델, SaaS등의 기능들이 장애, 오류 없이 원활히 활용될 수 체계, 방안 제시해야 함', 'REQ-AI-BA-0063', 'PER-001', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.876456', '2026-01-11 03:12:15.876462', '2026-01-31', '생성형 AI 질의응답 시간 정의 및 동시 접속자 정의', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '생성형 AI 질의응답 시간 및 동시 접속자', '-', e'○ 질의응답은 사용자가 요청한 시간으로부터 감독원과 협의한 시간 이내에 응답을 시작
* 예외사항 : 대량(1,000건)의 데이터에 대한 검색요청, 문서 생성 및 요약서비스, 대량의 BATCH성 작업요청 및 한 개 이상의 큰이미지(500KB 이상) 혹은 동영상을 가지고 있는 화면에 적용되지 않음', 'REQ-AI-BA-0064', 'PER-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.876778', '2026-01-11 03:12:15.876784', '2026-01-31', '생성형 AI 질의응답 시간 정의 및 동시 접속자 정의', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '생성형 AI 질의응답 시간 및 동시 접속자', '-', '○ 생성형 AI의 응답에 대기하고 있는 경우 사용자가 이를 인지할 수 있도록 알림 제공', 'REQ-AI-BA-0065', 'PER-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.877014', '2026-01-11 03:12:15.877020', '2026-01-31', '생성형 AI 질의응답 시간 정의 및 동시 접속자 정의', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '생성형 AI 질의응답 시간 및 동시 접속자', '-', '○ 사용자가 입력한 데이터 형식의 모든 오류도 사용자가 시스템에 해당정보를 입력한 지 감독원과 협의한 시간 이내에 적절한 오류메시지를 사용자에게 제시', 'REQ-AI-BA-0066', 'PER-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.877260', '2026-01-11 03:12:15.877265', '2026-01-31', '생성형 AI 질의응답 시간 정의 및 동시 접속자 정의', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '생성형 AI 질의응답 시간 및 동시 접속자', '-', '○ 임의의 선택 기준이 허용되는 대량의 데이터에 대한 질의는 적절한 상태 값 및 메시지를 사용자에게 전달', 'REQ-AI-BA-0067', 'PER-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.877520', '2026-01-11 03:12:15.877527', '2026-01-31', '생성형 AI 질의응답 시간 정의 및 동시 접속자 정의', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '생성형 AI 질의응답 시간 및 동시 접속자', '-', e'○ 동시 접속자 수 : 200명 이상(동시 질의수 : 100개 이상)
- 동시 질의 수가 100명 이상을 가정하여 설계할 것', 'REQ-AI-BA-0068', 'PER-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.877781', '2026-01-11 03:12:15.877787', '2026-01-31', 'HW, SW 성능 검증', 'ㅇ 사업개발', 'PoC(개념검증 및 실증)', '-', e'○ 데이터 허브, 생성형AI, 클라우드 서비스 포털 중 성능·호환성 확보의 검증이 필요한 경우, PoC 대상 및 수행방안을 감독원과 협의하여 제시해야 하며, PoC는 모든 과정을 수행사가 부담하여 추진하는 것으로 한다.
- 생성형 AI 경우, 구축 혹은 도입 전 우리 공사 데이터를 기반으로 프로토 모델을 실제 모델의 구동 여부와 성능요구 조건의 GAP을 분석하여 구축될 수 있도록 하여야 하며, 실증에 관련된 제반된 비용은 수행사가 부담하여야 한다.', 'REQ-AI-BA-0069', 'PER-005', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.878118', '2026-01-11 03:12:15.878124', '2026-01-31', 'HW, SW 성능 검증', 'ㅇ 사업개발', 'PoC(개념검증 및 실증)', '-', '○ 최적의 디지털플랫폼 구축을 위하여 제안사가 제시한 HW, SW을 대상으로 도입 전 발주사가 성능검증을 요구할 경우 제안사는 객관적인 성능측정 보고서를 작성하고 결과를 제출해야 함', 'REQ-AI-BA-0070', 'PER-005', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.878401', '2026-01-11 03:12:15.878407', '2026-01-31', '생성형 AI 윤리성·안정성 정의', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '생성형 AI 윤리성·안정성', '-', e'○ 생성형 AI 윤리성·안정성 정의
- ‘인공지능(AI) 윤리기준’이 제시하는 3대 기본원칙과 10대 핵심요건을 실천하기 위해 과기정통부·정보통신정책연구원에서 발표한 ‘2023 인공지능 윤리기준 실천을 위한 자율점검표(안)’상의 점검문항에 대한 자율점검표 및 각 점검항목에 대한 구체적 실천내용을 기술한 문서 제시
- 정부에서 발간한 「인공지능(AI) 개발·서비스를 위한 공개된 개인정보 처리 안내서」, 「인공지능 인권영향평가 도구」에서 제시한 점검 문항에 및 실천방안에 대한 제시', 'REQ-AI-BA-0071', 'DAR-007', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.878688', '2026-01-11 03:12:15.878694', '2026-01-31', '테스트 방안 요구사항', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '테스트 방안', '-', e'○ 업무 단위별 단위시험, 통합시험, 인수 테스트 등 방안 제시
- 성능 및 품질 요건을 충족할 수 있는 테스트 방안 수립
- 적합한 테스트 시나리오 및 테스트 데이터(오류 데이터 포함) 준비
- 단위, 통합, 시스템(성능) 테스트와 오류 감소 방안을 제시', 'REQ-AI-BA-0072', 'TER-001', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.879020', '2026-01-11 03:12:15.879025', '2026-01-31', '테스트 방안 요구사항', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '테스트 방안', '-', '○ 지속적인 테스트를 시행하고 테스트 결과 모니터링 및 반영', 'REQ-AI-BA-0073', 'TER-001', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.879311', '2026-01-11 03:12:15.879317', '2026-01-31', '테스트 방안 요구사항', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '테스트 방안', '-', '○ 도입되는 모든 S/W는 종합적인 테스트를 시행하고 결과를 제출', 'REQ-AI-BA-0074', 'TER-001', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.879603', '2026-01-11 03:12:15.879609', '2026-01-31', '단위, 통합 테스트 준수사항', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '테스트 수행', '-', e'○ 테스트 시나리오 작성, 수행 및 결과서 제출
- 시스템 테스트는 실 운영 환경에서 테스트를 수행하고, 시나리오를 통해 요구되는 성능요건을 충족', 'REQ-AI-BA-0075', 'TER-002', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.879940', '2026-01-11 03:12:15.879945', '2026-01-31', '단위, 통합 테스트 준수사항', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '테스트 수행', '-', '○ 요구사항 기능충족과 사용자 편의성 등 확인', 'REQ-AI-BA-0076', 'TER-002', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.880220', '2026-01-11 03:12:15.880226', '2026-01-31', '안정적 운영을 위한 시범운영 시행', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시범운영', '-', '○ 클라우드, 데이터허브, 생성형 AI 등 신기술 도입에 따른 안정적인 서비스 운영을 위한 방안을 제시해야 함', 'REQ-AI-BA-0077', 'TER-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.880482', '2026-01-11 03:12:15.880488', '2026-01-31', '안정적 운영을 위한 시범운영 시행', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시범운영', '-', '○ 인수시험 완료 후 시범운영(안정화) 수행', 'REQ-AI-BA-0078', 'TER-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.880736', '2026-01-11 03:12:15.880741', '2026-01-31', '안정적 운영을 위한 시범운영 시행', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시범운영', '-', e'○ 시범운영 및 안정화 기간에 대한 구체적인 계획 제시
- 일정, 목표, 참여 대상과 범위, 시스템 구성 방안 등
- 시범운영 기간 중 성능저하 프로그램의 튜닝 방안', 'REQ-AI-BA-0079', 'TER-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.880997', '2026-01-11 03:12:15.881002', '2026-01-31', '안정적 운영을 위한 시범운영 시행', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시범운영', '-', '○ 테스트를 통해 발견되는 결함이 해소될 때까지 지속적인 테스트를 수행하고, 테스트 결과에 대한 이력을 관리', 'REQ-AI-BA-0080', 'TER-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.881242', '2026-01-11 03:12:15.881247', '2026-01-31', '안정적 운영을 위한 시범운영 시행', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시범운영', '-', '○ 시스템 개발 완료 후 최소 30일 이상 시범운영 수행', 'REQ-AI-BA-0081', 'TER-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.881516', '2026-01-11 03:12:15.881522', '2026-01-31', '안정적 운영을 위한 시범운영 시행', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시범운영', '-', '○ 정식 서비스 후 2개월 안정화 기간 지원방안 제시', 'REQ-AI-BA-0082', 'TER-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.881789', '2026-01-11 03:12:15.881795', '2026-01-31', '디지털플랫폼 구축사업 수행 시 취약점 점검 및 조치에 대한 요건', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '보안 취약점에 대한 점검 및 보완 조치', '-', '○ 시스템에 대한 자체 또는 국정원 등 상급기관 보안성 검토를 통해 도출된 취약점 및 권고사항에 대하여 조치방안 제시 후 개선조치를 수행해야 한다.', 'REQ-AI-BA-0083', 'SER-006', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.882062', '2026-01-11 03:12:15.882067', '2026-01-31', '디지털플랫폼 구축사업 수행 시 취약점 점검 및 조치에 대한 요건', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '보안 취약점에 대한 점검 및 보완 조치', '-', e'시스템 개발 시 보안약점이 없도록 소프트웨어 개발보안 가이드(행정
안전부)를 적용하여 개발하고 점검결과를 제출해 K-water 정보보안담당자의 확인을 받아야 한다.
- 시큐어코딩 보안취약점 확인 점검표(설계단계) 【별지 제8호 서식】를 준수하여 설계단계 산출물을 작성
- 시큐어코딩 보안취약점 확인 점검표(구현단계) 【별지 제9호 서식】및 웹 보안취약점 확인 점검표 【별지 제10호 서식】를 준수하여 소스코드 작성', 'REQ-AI-BA-0084', 'SER-006', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.882398', '2026-01-11 03:12:15.882408', '2026-01-31', '디지털플랫폼 구축사업의 기능 검증에 대한 요건', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시험운영을 통한 기능 구현 정확성 검증', '-', '○ 준공 전 시험운영 기간을 통해 기능 구현의 정확성을 검증해야 하며, 보완사항이 도출될 경우 이에 대한 개선 조치를 해야 한다.', 'REQ-AI-BA-0085', 'QUR-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.882765', '2026-01-11 03:12:15.882771', '2026-01-31', '디지털플랫폼 구축사업의 기능 검증에 대한 요건', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시험운영을 통한 기능 구현 정확성 검증', '-', '○ 각 요구사항(기능)의 검증 및 활용을 통해 예상된 결과가 도출되었을 경우 요구사항을 제공한 것으로 평가한다.', 'REQ-AI-BA-0086', 'QUR-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.882978', '2026-01-11 03:12:15.882984', '2026-01-31', '디지털플랫폼 구축사업의 기능 검증에 대한 요건', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시험운영을 통한 기능 구현 정확성 검증', '-', '○ 초기 요구사항에 대해 변경이 필요한 경우 감독원과 협의하여 결정한다.', 'REQ-AI-BA-0087', 'QUR-003', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.883185', '2026-01-11 03:12:15.883190', '2026-01-31', '디지털플랫폼 구축사업의 운영 시 안정성과 유연성에 대한 요건', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '신뢰성 및 운영 유연성 보장', '-', e'○ 유지관리 용이성, 상호운영성, 이식성 등을 보장하여 개발해야 한다.
- 새로운 기능 구현을 위해 필요한 경우 업그레이드가 용이해야 하며, 보안사고 예방, 성능 개선 등을 위해 필요 시 패치를 제공', 'REQ-AI-BA-0088', 'QUR-004', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.883445', '2026-01-11 03:12:15.883450', '2026-01-31', 'Kwater 디지털플랫폼 개발 및 운영 시 필요한 요건', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시스템 개발 및 운영 조건', '-', e'○ K-water 시스템 운영 사용 환경 및 정보인프라에 맞추어 시스템 구축
- 개발 시 기존 아키텍처 구조와 유사한 구조로 개발을 수행하여 유지관리에 무리가 없도록 수행', 'REQ-AI-BA-0089', 'PMR-005', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.883727', '2026-01-11 03:12:15.883734', '2026-01-31', 'Kwater 디지털플랫폼 개발 및 운영 시 필요한 요건', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시스템 개발 및 운영 조건', '-', '○ 개발이 체계적으로 추진되도록 각 개발 단계별 활용할 도구(분석 및 설계 도구, 개발도구 등)와 기법의 적정성을 제시해야 한다.', 'REQ-AI-BA-0090', 'PMR-005', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.884028', '2026-01-11 03:12:15.884035', '2026-01-31', 'Kwater 디지털플랫폼 개발 및 운영 시 필요한 요건', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시스템 개발 및 운영 조건', '-', '○ 구축되는 정보시스템은 365일 24시간 상시 서비스를 목표로 무중단 서비스 체제로 운영되어야 한다.', 'REQ-AI-BA-0091', 'PMR-005', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.884287', '2026-01-11 03:12:15.884292', '2026-01-31', 'Kwater 디지털플랫폼 개발 및 운영 시 필요한 요건', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '시스템 개발 및 운영 조건', '-', '○ K-water에서 운영 중인 인프라와 호환 및 이를 활용하여 운영한다.', 'REQ-AI-BA-0092', 'PMR-005', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.884539', '2026-01-11 03:12:15.884544', '2026-01-31', 'Kwater 디지털플랫폼 운영 시 하자보수에 대한 요건', e'ㅇ PO 제공 가능
ㅇ SI개발은 이행 제공', '하자보수 일반사항', '-', e'○ 무상 하자보수 기간은 K-water의 검사에 의하여 사업의 완성을 확인한 후 1년간으로 하며, 이에 대한 제반사항은 계약상대자가 부담해야 한다.
- 시스템 운영 과정에서 발생한 오류 등은 하자 보증기간 동안 사업자가 무상으로 개선', 'REQ-AI-BA-0093', 'PSR-001', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.884791', '2026-01-11 03:12:15.884797', '2026-01-31', 'Kwater 디지털플랫폼 운영 시 하자보수에 대한 요건', e'ㅇ SI개발(이행)
- LLMOps로 인한 요구 조건(성능 등) 미충족 시 K Studio 업데이트 지원', '하자보수 일반사항', '-', e'○ 용역성과품의 하자발생 시 이를 즉시 조치하여야 하며, 조치결과를 보고서로 제출하여 감독원의 승인을 받아야 한다.
- 하자보수 지원은 K-water의 근무시간을 기준으로 하되 요구가 있을 경우 근무시간 및 야간시간/휴일에도 지원 필요', 'REQ-AI-BA-0094', 'PSR-001', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.885052', '2026-01-11 03:12:15.885057', '2026-01-31', 'Kwater 디지털플랫폼 구축 시 기술 및 교육에 대한 요건', 'ㅇ SI개발(이행)', '기술이전 및 교육훈련', '-', '○ 사업 추진에 필요한 SW, 정보기술(IT), 성과물 등에 대한 기술이전 및 교육훈련 계획(방법, 일정, 조직, 내용 등)을 수립하여 감독원과 협의 후 교육을 실시해야 한다.', 'REQ-AI-BA-0095', 'PSR-002', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.885279', '2026-01-11 03:12:15.885285', '2026-01-31', 'Kwater 디지털플랫폼 구축 시 기술 및 교육에 대한 요건', 'ㅇ SI개발(이행)', '기술이전 및 교육훈련', '-', '○ 사업 완료 후 K-water에서 원활한 운영을 위한 기술지원 전략 등을 제시하여야 한다.', 'REQ-AI-BA-0096', 'PSR-002', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.885498', '2026-01-11 03:12:15.885502', '2026-01-31', 'Kwater 디지털플랫폼 구축 시 기술 및 교육에 대한 요건', 'ㅇ SI개발(이행)', '기술이전 및 교육훈련', '-', '○ 기술이전 및 교육에 따른 제반 비용은 계약상대자가 부담하여야 하며, 피교육자의 파견 및 체재비는 발주자가 부담한다.', 'REQ-AI-BA-0097', 'PSR-002', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.885751', '2026-01-11 03:12:15.885755', '2026-01-31', 'Kwater 디지털플랫폼 구축 시 기술 및 교육에 대한 요건', 'ㅇ SI개발(이행)', '기술이전 및 교육훈련', '-', '○ 교육은 K-water에서 실시함을 원칙으로 하며, 기타 지역에서 실시하고자 할 경우에는 감독원과 협의 후 결정한다.', 'REQ-AI-BA-0098', 'PSR-002', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.886014', '2026-01-11 03:12:15.886016', '2026-01-31', '생성형 AI 연계 데이터 시각화 방안', e'ㅇ 이행 SI 개발
- 스캐터X 연계', '생성형 AI 연계 데이터 시각화', '-', e'2. 기능에 대한 사항
생성형 AI의 결과 데이터를 각종 차트, GIS 등과 손쉽게 연계 및 시각화가 가능하도록 하며, 비전문가도 활용할 수 있는 노코딩 제작기법 등을 검토하여 수도·수자원 등 플랫폼 내 다양한 시스템들이 활용할 수 있는 데이터 시각화를 구축', 'REQ-AI-BA-0099', '기술협상', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.886217', '2026-01-11 03:12:15.886222', '2026-01-31', '최신 모델 서빙 및 외부 생성형 AI API 관리', e'# 최신/신규 sLLM을 LLMOps에서 Serving 하도록 연동 및 기존 서비스 LLM모델 교체 기능 제공
ㅇ PO 제공 가능
# 외부 생성형 AI API 관리 기능 제공(API키 통합관리, API키 별 사용량, 비용 집계, 사용 제한 기능, 로드 밸런싱)', '멀티LLM(내부‧외부) 최적 활용‧관리', 'BA: 오픈소스모델 학습/서빙 기능 제공', e'2. 기능에 대한 사항
라. 생성형 AI 개발과 서비스를 위해 멀티LLM(내부‧외부)을 최적 활용‧관리토록 구현하며, 내부 데이터 학습과 RAG 구축의 검증‧성능개선 방안을 제시하고, 최신 LLM모델의 이용‧업데이트 등에도 원활히 대응할 수 있도록 구축', 'REQ-AI-BA-0100', '기술협상', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.886396', '2026-01-11 03:12:15.886399', '2026-01-31', 'Huggingface 등재 모델 미세조정', e'# Huggingface에 등재된 LLM모델 Serving 및 Finetuning 기능 제공
ㅇ PO 제공 가능', '멀티LLM(내부‧외부) 최적 활용‧관리', 'BA: 오픈소스모델 학습/서빙 기능 제공', e'2. 기능에 대한 사항
라. 생성형 AI 개발과 서비스를 위해 멀티LLM(내부‧외부)을 최적 활용‧관리토록 구현하며, 내부 데이터 학습과 RAG 구축의 검증‧성능개선 방안을 제시하고, 최신 LLM모델의 이용‧업데이트 등에도 원활히 대응할 수 있도록 구축', 'REQ-AI-BA-0101', '기술협상', '');
INSERT INTO public.requirements (created_at, updated_at, deadline, definition, implementation_opinion, name, poba_opinion, request_content, requirement_id, rfp_id, tech_innovation_opinion) VALUES ('2026-01-11 03:12:15.850516', '2026-01-11 04:43:08.525633', '2026-01-31', '생성형 AI 기본사항 개념 정의', e'ㅇ PO 제공 가능
- 믿음 Pro기반 기혁제공 모델 탑재
- K Studio에서 버전별로 제공할 수 있는 모델 모두 납품 예정
- 외부 생성형 AI 모델 활용
- 모델별 성능 비교 및 고성능 모델 위주 활용 예정', '데이터허브 – 생성형 AI 모델', 'BA: PO/Lab 확인 필요', e'○ 생성형 AI LLM 사전학습 및 미세조정이(한국어, 한국의 현재 상황 기준)이 완료된 우수한 모델 제공
- 생성형 AI 모델(LLM Engine)
- 멀티 LLM 알고리즘 활용을 위한 기반 환경 지원
- 생성형 AI를 위한 LLMOps 등 생성형 AI 학습 및 RAG를 위한 관련프로그램 포함
- 구축 착수 직전까지 외부 최신 데이터 학습 필요
○ 파라미터 수 : 40B 이상', 'REQ-AI-BA-0002', 'SFR-DHUB-008', e'제공가능 모델 (이행/PO정의 시)
-믿음 K Pro 2.4.0 (41B)
-믿음 K pro 2.5 (32B, 12월)
-라마 K (74B)');
