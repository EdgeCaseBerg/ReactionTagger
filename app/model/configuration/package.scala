package model

import play.api.libs.json._
import java.nio.file.Path
import java.nio.file.Paths

package object configuration {
	implicit val pathWrites = new Format[Path] {
		def writes(o: Path) = JsString(o.toAbsolutePath.toString)
		def reads(jsValue: JsValue) = {
			jsValue.validate[String] match {
				case JsSuccess(str, path) =>
					JsSuccess(Paths.get(str), path)
				case JsError(errors) => JsError(errors)
			}
		}
	}
}