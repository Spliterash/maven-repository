package ru.spliterash.mavenrepository.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import ru.spliterash.mavenrepository.repository.result.FileInfo

data class InfoResponse(
	val name: String,
	val type: FileInfo.Type,
	val files: List<Item>,
) {
	data class Item(
		val name: String,
		val type: FileInfo.Type,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		val contentType: String? = null,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		val contentLength: Long? = null
	)
}


