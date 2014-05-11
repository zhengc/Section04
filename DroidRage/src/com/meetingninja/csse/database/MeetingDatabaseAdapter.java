/*******************************************************************************
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.meetingninja.csse.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import objects.Meeting;
import objects.User;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.database.BaseDatabaseAdapter.IRequest;

public class MeetingDatabaseAdapter extends BaseDatabaseAdapter {
	private final static String TAG = MeetingDatabaseAdapter.class.getSimpleName();

	public static String getBaseUrl() {
		return BASE_URL + "Meeting";
	}

	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}

	public static Meeting getMeetingInfo(String meetingID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath(meetingID).build().toString();

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);
		conn.getResponseCode();
		String response = getServerResponse(conn);
		JsonNode meetingNode = MAPPER.readTree(response);
		Meeting ret = parseMeeting(meetingNode);
		ret.setID(meetingID);
		return ret;
	}

	public static Meeting editMeeting(Meeting meeting) throws IOException {
		String meetingID = meeting.getID();
		String keyValue = Keys.Meeting.ID;
		String titlePayload = getEditPayload(meetingID, Keys.Meeting.TITLE,meeting.getTitle(),keyValue);
		String startdateTimePayload = getEditPayload(meetingID,Keys.Meeting.DATETIME, meeting.getStartTime(),keyValue);
		String locationPayload = getEditPayload(meetingID,Keys.Meeting.LOCATION, meeting.getLocation(),keyValue);
		String endDateTimePayload = getEditPayload(meetingID,Keys.Meeting.OTHEREND, meeting.getEndTime(),keyValue);
		String descPayload = getEditPayload(meetingID, Keys.Meeting.DESC,meeting.getDescription(),keyValue);

		ByteArrayOutputStream json = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(json);
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);

		jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		jgen.writeStartObject();
		jgen.writeStringField(Keys.Meeting.ID, meetingID);
		jgen.writeStringField("field", Keys.Meeting.ATTEND);
		jgen.writeArrayFieldStart("value");
		for (User member : meeting.getAttendance()) {
			jgen.writeStartObject();
			jgen.writeStringField(Keys.User.ID, member.getID());
			jgen.writeEndObject();
		}
		jgen.writeEndArray();
		jgen.writeEndObject();
		jgen.close();
		String attendancePayload = json.toString("UTF8");
		ps.close();
		String _url = getBaseUri().appendPath(meetingID).build().toString();
		// Get server response
		sendSingleEdit(titlePayload,_url);
		sendSingleEdit(startdateTimePayload,_url);
		sendSingleEdit(locationPayload,_url);
		sendSingleEdit(endDateTimePayload,_url);
		sendSingleEdit(descPayload,_url);
		String response = sendSingleEdit(attendancePayload,_url);
		final JsonNode meetingNode = MAPPER.readTree(response);
		Meeting m = parseMeeting(meetingNode);
		return m;
	}

	public static Meeting createMeeting(String userID, Meeting m)throws IOException, MalformedURLException {
		// Server URL setup
		String _url = getBaseUri().appendPath(userID).build().toString();

		// establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("POST");
		addRequestHeader(conn, true);

		// prepare POST payload
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);

		// Build JSON Object
		jgen.writeStartObject();
		jgen.writeStringField(Keys.User.ID, userID);
		jgen.writeStringField(Keys.Meeting.TITLE, m.getTitle());
		jgen.writeStringField(Keys.Meeting.LOCATION, m.getLocation());
		jgen.writeStringField(Keys.Meeting.DATETIME, m.getStartTime());
		jgen.writeStringField(Keys.Meeting.OTHEREND, m.getEndTime());
		jgen.writeStringField(Keys.Meeting.DESC, m.getDescription());
		jgen.writeArrayFieldStart(Keys.Meeting.ATTEND);
		// TODO: Add attendees to meeting
		for (User attendee : m.getAttendance()) {
			jgen.writeStartObject();
			jgen.writeStringField(Keys.User.ID, attendee.getID());
			jgen.writeEndObject();
		}
		jgen.writeEndArray();
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		// prepare to get the id of the created Meeting
		JsonNode responseMap;
		m.setID(404);
		Meeting created = new Meeting(m);
		/*
		 * result should get valid={"meetingID":"##"}
		 */
		// String result = new String();
		if (!response.isEmpty()) {
			responseMap = MAPPER.readTree(response);
			if (responseMap.has(Keys.Meeting.ID))
				created.setID(responseMap.get(Keys.Meeting.ID).asText());
		}

		// if (!result.equalsIgnoreCase("invalid"))
		// created.setID(result);

		conn.disconnect();
		return created;
	}
	
	public static Boolean deleteMeeting(String meetingID) throws IOException {
		String _url = getBaseUri().appendPath(meetingID).build().toString();
		return deleteItem(_url);
	}

	public static Meeting parseMeeting(JsonNode node) {
		logPrint(node.toString());
		final Meeting m = new Meeting();
		m.setTitle(node.get(Keys.Meeting.TITLE).asText());
		m.setLocation(node.get(Keys.Meeting.LOCATION).asText());
		m.setStartTime(node.get(Keys.Meeting.DATETIME).asText());
		m.setEndTime(node.get(Keys.Meeting.OTHEREND).asText());
		m.setDescription(node.get(Keys.Meeting.DESC).asText());
		JsonNode attendance = node.get(Keys.Meeting.ATTEND);
		if (attendance != null && attendance.isArray()) {
			for (final JsonNode attendeeNode : attendance) {
				User user = new User();
				user.setID(attendeeNode.get(Keys.User.ID).asText());
				try {
					user = UserDatabaseAdapter.getUserInfo(user.getID());
				} catch (IOException e) {
					e.printStackTrace();
				}
				m.addAttendee(user);
			}
		} else
			Log.e(TAG, "Error: Unable to parse meeting attendance");

		return m;
	}
}
