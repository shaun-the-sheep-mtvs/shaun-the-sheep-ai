package org.mtvs.backend.routine.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mtvs.backend.routine.domain.dto.RoutineDto;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestRoutinesListDto {
    List<RoutineDto> routines;
}
