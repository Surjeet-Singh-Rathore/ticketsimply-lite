package com.bitla.ts.domain.pojo.block_configuration_model

data class BlockConfigurationModel(
    val blocking_types: List<String>,
    val user_types: List<UserType>
)