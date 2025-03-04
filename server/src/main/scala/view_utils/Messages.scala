package view_utils

import io.circe
import io.circe.{parser, Codec, Decoder, Encoder}
import io.circe.syntax.*
import io.circe.generic.semiauto.*
import view_utils.Messages.MessageType

case class Messages(
    messages: List[Messages.Item]
) {
  def add(
      messageType: MessageType,
      text: String
  ): Messages = {
    Messages(
      Messages.Item(
        messageType,
        text
      ) :: messages
    )
  }

  def toJson: String = {
    import Messages.Codecs.*

    messages.asJson.noSpaces
  }
}

object Messages {
  final val SESSION_KEY = "messages"

  def apply(): Messages = Messages(List.empty)

  def fromJson(json: String): Either[circe.Error, Messages] = {
    import Messages.Codecs.*
    parser.decode[Messages](json)
  }

  enum MessageType {
    case INFO
    case WARNING
    case ERROR
  }

  case class Item(
      messageType: MessageType,
      text: String
  )

  private object Codecs {
    implicit val messageTypeCodec: Codec[MessageType] = deriveCodec[MessageType]
    implicit val itemCodec: Codec[Item] = deriveCodec[Item]
    implicit val messagesCodec: Codec[Messages] = deriveCodec[Messages]
  }
}
