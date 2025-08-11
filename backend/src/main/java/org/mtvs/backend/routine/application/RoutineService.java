package org.mtvs.backend.routine.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.routine.domain.dto.request.RequestRoutineAllDto;
import org.mtvs.backend.routine.domain.dto.request.RequestRoutinesListDto;
import org.mtvs.backend.routine.domain.dto.RoutinesDto;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.user.repository.UserRepository;
import org.mtvs.backend.routine.domain.entity.Routine;
import org.mtvs.backend.routine.domain.entity.RoutineGroup;
import org.mtvs.backend.routine.repository.RoutineGroupRepository;
import org.mtvs.backend.routine.repository.RoutineRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {

	private final RoutineRepository routineRepository;
	private final UserRepository userRepository;
	private final RoutineGroupRepository routineGroupRepository;

	@Transactional
	public void createRoutine(RequestRoutinesListDto routinesDTO, String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		RoutineGroup routineGroup = new RoutineGroup(
			user.getId()
		);

		routineGroupRepository.save(routineGroup);
		List<Routine> routines = routinesDTO.getRoutines().stream()
			.map(routineDTO -> new Routine(
				routineDTO.getName(),
				routineDTO.getTime(),
				routineDTO.getKind(),
				routineDTO.getMethod(),
				routineDTO.getOrders(),
				user,
				routineGroup.getId()

			))
			.collect(Collectors.toList());
		// 루틴 저장
		routineRepository.saveAll(routines);
	}

	// 사용자의 모든 루틴을 조회하는 메소드
	@Transactional
	public List<RequestRoutineAllDto> getAllRoutines(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		List<RequestRoutineAllDto> dtos = new ArrayList<>();
		routineRepository.findRoutinesByUser(user).forEach(routine -> {
			dtos.add(new RequestRoutineAllDto(routine));
		});
		return dtos;
	}

	/* step2. 기존 루틴 조회 */
	public List<RoutinesDto> getRoutineList(String userId) {
		return routineRepository.findRoutinesByUserId(userId);
	}

	public List<RoutinesDto> getAllRoutineList(String userId) {
		return routineRepository.findAllRoutinesByUserId(userId);
	}

	public void deleteRoutine(long groupId, CustomUserDetails userDetails) {
		routineRepository.deleteAll(
			routineRepository.findRoutinesByRoutineGroupIdAndUser(groupId, userDetails.getUser())
		);
	}

	public void updateRoutine(Long routineGroupId, CustomUserDetails userDetails, List<RoutinesDto> routinesDtos) {

		List<Routine> routines = routineRepository.getRoutinesByRoutineGroupId(routineGroupId);
		for (Routine routine : routines) {
		}
		routineRepository.saveAll(routines);
	}
}
