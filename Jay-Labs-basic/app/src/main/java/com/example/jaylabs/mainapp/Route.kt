package com.example.jaylabs.mainapp

sealed class Route(val route: String) {
   data object AuthScreen : Route("auth_screen")
   data object HomeScreen : Route("home_screen")
   data object LogIn : Route("log_in")
   data object SignUp : Route("sign_up")
   data object PastReports:Route("past_reports")
   data object ReportDetails:Route("report_detail")

}