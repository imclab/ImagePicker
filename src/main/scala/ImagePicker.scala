import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import scala.collection._

case class Result(title: String, urls: List[String])

object ImagePicker {
  private def getParentA(element: org.jsoup.nodes.Element): Option[org.jsoup.nodes.Element] = {
    element.parent() match {
      case e:org.jsoup.nodes.Element if e.tagName() == "a" => Some(e)
      case e:org.jsoup.nodes.Element => getParentA(e)
      case null => None
    }
  }

  private def getURLFromImg(img: org.jsoup.nodes.Element): String = {
    getParentA(img) match {
      case Some(a) => {
        a.attr("href") match {
          // Ignore affiliate images
          case href if href.matches(""".*//www\.amazon\..*""") => ""
          case href if href.matches(""".*//h\.accesstrade\.net/.*""") => ""
          case href if href.matches(""".*//b\.hatena\.ne\.jp/.*""") => ""
          case href if href.matches(""".*//.*\.microad\.jp/.*""") => ""
          case href if href.matches(""".*//rss\.tc/.*""") => ""
          case href if href.matches(""".*//www\.ziyu\.net/.*""") => ""
          case href if href.matches(""".*//twitter\.com/.*""") => ""
          case href if href.matches(""".*//d\.href\.asia/.*""") => ""
          case href if href.matches(""".*//clap\.fc2\.com/.*""") => ""
          case href if href.matches(""".*//www\.linkwithin\.com/.*""") => ""
          case href if href.matches(""".*//opr\.formulas\.jp/.*""") => ""

          // Return img[src] if link is javascript
          case href if href.matches("""javascript:.*""") => img.attr("src")

          case href => href
        }
      }
      case _ => img.attr("src")
    }
  }

  private def pickURLsFromElements(doc: org.jsoup.nodes.Element, selector: String): List[String] = {
    (doc.select(selector) map (e =>
      e.select("img") map (img =>
        getURLFromImg(img) match {
          // Ignore icons
          case url if url.matches(""".*//parts\.blog\.livedoor\.jp/.*""") => ""
          case url if url.matches(""".*//www\.assoc-amazon\.jp/.*""") => ""
          case url if url.matches(""".*//resize\.blogsys\.jp/.*""") => ""

          case url => url
        }
      )
    )).flatten.toList filter (url => ! url.isEmpty())
  }

  def pick(pageurl: String) = {
    val doc = Jsoup.connect(pageurl).get();

    var urls = mutable.MutableList[String]()

    // ------------------------------------------------------------
    // Remove noisy nodes

    // livedoor Blog
    doc.select("div.container div.article div.article-bottom") foreach (e => e.empty())

    // ------------------------------------------------------------
    // FC2 BLOG
    urls ++= pickURLsFromElements(doc, "div.ently_body")
    urls ++= pickURLsFromElements(doc, "div.entry_body")
    urls ++= pickURLsFromElements(doc, "div.entry-inner")
    urls ++= pickURLsFromElements(doc, "div#contents div#content-inner div#blog div.entry")

    // livedoor Blog
    urls ++= pickURLsFromElements(doc, "div.article-body")
    urls ++= pickURLsFromElements(doc, "div.article-body-more")
    urls ++= pickURLsFromElements(doc, "div.blogbody")
    urls ++= pickURLsFromElements(doc, "div.entry-body")
    urls ++= pickURLsFromElements(doc, "div.article_body")
    urls ++= pickURLsFromElements(doc, "div.article-main1")
    urls ++= pickURLsFromElements(doc, "div.article-main2")
    urls ++= pickURLsFromElements(doc, "div#main_col blockquote")
    urls ++= pickURLsFromElements(doc, "div#main_box3 div.contents")
    urls ++= pickURLsFromElements(doc, "div.container div.article")

    Result(doc.select("title").text, urls.toList)
  }

  def main(args: Array[String]) = {
    if (args.size > 0) {
      println("URL: " + args{0})
      val result = pick(args{0})
      println("Title: " + result.title)
      result.urls foreach (url => println("'" + url + "',"))
    }
  }
}
