package view_utils

import io.circe
import io.circe.derivation.{Configuration, ConfiguredCodec, ConfiguredEnumCodec}
import io.circe.syntax.*
import io.circe.{parser, Decoder, Encoder}
import view_utils.Messages.MessageType

object Messages {
  given Configuration = Configuration.default.withSnakeCaseMemberNames

  final val SESSION_KEY = "messages"

  def apply(): Messages = Messages(List.empty)

  def fromJson(json: String): Either[circe.Error, Messages] = {
    parser.decode[Messages](json)
  }

  enum MessageType derives ConfiguredEnumCodec {
    case INFO
    case WARNING
    case ERROR
  }

  case class Item(
      messageType: MessageType,
      text: String
  ) derives ConfiguredCodec
}

case class Messages(
    items: List[Messages.Item]
) derives ConfiguredCodec {
  def add(messageType: MessageType, text: String): Messages = {
    Messages(
      Messages.Item(
        messageType,
        text
      ) :: items
    )
  }

  def toJson: String = {
    this.asJson.noSpaces
  }
}
