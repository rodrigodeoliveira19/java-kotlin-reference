package com.demo.appfinanceiro.adapter.output.database.dynamodb

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun stringAttribute(value: String): AttributeValue = AttributeValue.builder().s(value).build()

fun Map<String, AttributeValue>.stringValue(key: String): String? = this[key]?.s()
