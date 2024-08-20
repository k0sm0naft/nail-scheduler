package fern.nail.art.nailscheduler.annotation;

import fern.nail.art.nailscheduler.dto.slot.SlotRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class TimeRangeValidator
        implements ConstraintValidator<RangeValidator, SlotRequestDto> {
    public static final int MINUTES_TO_ADD = 14;

    @Override
    public boolean isValid(SlotRequestDto slotDto, ConstraintValidatorContext context) {
        return slotDto.startTime().isBefore(slotDto.endTime())
                && slotDto.startTime().plusMinutes(MINUTES_TO_ADD).isBefore(slotDto.endTime());
    }
}
