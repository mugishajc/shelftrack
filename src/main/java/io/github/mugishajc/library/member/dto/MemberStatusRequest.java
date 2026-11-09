package io.github.mugishajc.library.member.dto;

import io.github.mugishajc.library.member.MemberStatus;
import jakarta.validation.constraints.NotNull;

public record MemberStatusRequest(@NotNull MemberStatus status) {
}
