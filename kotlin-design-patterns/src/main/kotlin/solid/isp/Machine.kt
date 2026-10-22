package org.example.solid.isp

interface Machine {
    fun print(document: String)
    fun scan(document: String)
    fun fax(document: String)
}