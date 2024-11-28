package samoyed.codegen.util

object CaseConverter {
  def toSnakeCase(s: String): String = {
    s.foldLeft("") { (acc, c) =>
      if (c.isUpper) {
        if (acc.isEmpty) {
          acc + c.toLower
        } else {
          acc + "_" + c.toLower
        }
      } else {
        acc + c
      }
    }
  }

  def toCamelCase(s: String): String = {
    s.split("_").foldLeft("") { (acc, w) =>
      if (acc.isEmpty) {
        w
      } else {
        acc + w.head.toUpper + w.tail
      }
    }
  }

  def toKebabCase(s: String): String = {
    s.foldLeft("") { (acc, c) =>
      if (c.isUpper) {
        if (acc.isEmpty) {
          acc + c.toLower
        } else {
          acc + "-" + c.toLower
        }
      } else {
        acc + c
      }
    }
  }
}
