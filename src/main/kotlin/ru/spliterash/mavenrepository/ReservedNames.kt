package ru.spliterash.mavenrepository

import java.util.stream.Stream

const val PUBLIC = "group"
val PRIVATE = listOf("private", "all")

val RESERVED_NAMES = Stream.of(Stream.of(PUBLIC), PRIVATE.stream()).flatMap { it }.toList()