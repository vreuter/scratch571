package flybrain571

sealed trait IPStatus
final case object Input extends IPStatus
final case object Positive extends IPStatus
final case object Negative extends IPStatus
