package com.example.palina.lr1.utils

import android.util.Xml
import com.example.palina.lr1.models.RssNew
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.lang.Exception

class XmlToRssFeedParser {
    companion object {
        fun parse(inputStream: InputStream): ArrayList<RssNew>? {
            var title: String? = null
            var url: String? = null
            var description: String? = null
            var date: String? = null
            var imageUrl: String? = null
            var isItem = false

            val items = ArrayList<RssNew>()

            try {
                val xmlPullParser = Xml.newPullParser()
                xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                xmlPullParser.setInput(inputStream, null)

                xmlPullParser.nextTag()
                while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                    val eventType = xmlPullParser.eventType
                    val name = xmlPullParser.name ?: continue
                    if (eventType == XmlPullParser.END_TAG) {
                        if (name.equals("item", ignoreCase = true)) {
                            if (title != null && url != null && description != null) {
                                if (isItem) {
                                    val item = RssNew(title = title, url = url, description = description,
                                        date = date ?: "", image = imageUrl ?: "")
                                    items.add(item)
                                }
                                title = null
                                url = null
                                description = null
                                date = null
                                imageUrl = null
                            }
                            isItem = false
                        }
                        continue
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (name.equals("item", ignoreCase = true)) {
                            isItem = true
                            continue
                        }
                    }

                    var result = ""
                    if (xmlPullParser.next() == XmlPullParser.TEXT) {
                        result = xmlPullParser.text
                        xmlPullParser.nextTag()
                    }

                    when {
                        name.equals("title", ignoreCase = true) -> title = result
                        name.equals("link", ignoreCase = true) -> url = result
                        name.equals("description", ignoreCase = true) -> description = result
                        name.equals("enclosure", ignoreCase = true) -> imageUrl = xmlPullParser.getAttributeValue(null, "url")
                        name.equals("thumbnail", ignoreCase = true) -> imageUrl = result
                        name.equals("pubDate", ignoreCase = true) -> date = result
                    }
                }

                return items
            }
            catch (e: Exception){ return null }
            finally {
                inputStream.close()
            }
        }
    }
}