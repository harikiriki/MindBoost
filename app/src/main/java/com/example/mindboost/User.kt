package com.example.mindboost

data class User(var nickname : String ?= null, var email : String ?= null,
                var password : String ?= null, var birthDate : String ?= null,
                var gender : String ?= null, var notifications: Boolean ?= true)
