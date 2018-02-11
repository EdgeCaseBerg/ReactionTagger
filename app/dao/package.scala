package object dao {
	sealed trait FileExists
	case object DoesNotExist extends FileExists
	case object ExistsInQueue extends FileExists
	case object ExistsInTagged extends FileExists
	case object ExistsInBoth extends FileExists
}