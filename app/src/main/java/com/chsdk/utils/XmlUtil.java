package com.chsdk.utils;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.chsdk.model.login.SourceInfo;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月17日
 *          <p>
 */
public class XmlUtil {
	private static final String USRE_ID = "UserID";
	private static final String SOURCE_ID = "SourceID";
	private static final String VERSION = "Version";

	public static SourceInfo sourceInfodataParser(InputStream in) throws IOException {
		SourceInfo sourceInfo = new SourceInfo();
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(in, "UTF-8");
			int evtType = xpp.getEventType();
			while (evtType != XmlPullParser.END_DOCUMENT) {
				if (evtType == XmlPullParser.START_TAG) {
					if (sourceInfo == null) {
						 sourceInfo = new SourceInfo();
					}
					
					String tag = xpp.getName();
					if (tag.equals(USRE_ID)) {
						sourceInfo.userId = Integer.valueOf(safeNextText(xpp)).intValue();
					} else if (tag.equals(SOURCE_ID)) {
						sourceInfo.sourceId = Integer.valueOf(safeNextText(xpp)).intValue();
					} else if (tag.equals(VERSION)) {
						sourceInfo.version = Float.valueOf(safeNextText(xpp));
						return sourceInfo;
					}
				}
				evtType = xpp.next();
			}
		} catch (XmlPullParserException e) {
		}

		return sourceInfo;
	}

	private static String safeNextText(XmlPullParser parser) throws XmlPullParserException, IOException {
		String result = parser.nextText();
		if (parser.getEventType() != XmlPullParser.END_TAG) {
			parser.nextTag();
		}
		return result;
	}
}
