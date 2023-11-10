package com.intelix.challenge.service.app.criteria;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SortCriteriaDateParam {

	@NotNull
	@NotBlank
	private String sortBy;

	@NotNull
	@NotBlank
	private String sortDirection;

	@NotNull
	@DateTimeFormat(iso = ISO.DATE, pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date date1;

	@NotNull
	@DateTimeFormat(iso = ISO.DATE, pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date date2;

}
