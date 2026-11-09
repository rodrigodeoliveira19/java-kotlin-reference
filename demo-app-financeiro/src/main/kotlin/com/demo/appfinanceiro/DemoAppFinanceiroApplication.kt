package com.demo.appfinanceiro

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext

@SpringBootApplication
class DemoAppFinanceiroApplication

fun main(args: Array<String>) {
	ApplicationContext
	runApplication<DemoAppFinanceiroApplication>(*args)
}
