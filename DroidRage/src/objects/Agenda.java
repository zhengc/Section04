package objects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonInclude(JsonInclude.Include.NON_NULL)
// Generated by : http://www.jsonschema2pojo.org/
@JsonPropertyOrder({ "agendaID", "title", "meeting", "content" })
public class Agenda implements IJSONObject {
	@JsonProperty("agendaID")
	private String agendaID;
	@JsonProperty("title")
	private String title;
	@JsonProperty("content")
	private ArrayList<Topic> topics = new ArrayList<Topic>();
	@JsonProperty("meeting")
	private String attachedMeetingID;

	public Agenda() {
		// Required empty constructor
	}

	public Agenda(String title) {
		this.title = title;
	}

	public Agenda(Agenda create) {
		setID(create.getID());
		setTitle(create.getTitle());
		setAttachedMeetingID(create.getAttachedMeetingID());
		setTopics(create.getTopics());
	}

	@JsonProperty("agendaID")
	public void setID(String id) {
		int testInt = Integer.valueOf(id);
		setID(testInt);
	}

	@JsonIgnore
	private void setID(int id) {
		this.agendaID = Integer.toString(id);
	}

	@JsonProperty("agendaID")
	public String getID() {
		return this.agendaID;
	}

	@JsonProperty("title")
	public String getTitle() {
		return (title != null && !title.isEmpty()) ? title : "";
	}

	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	public void addTopic(Topic topic) {
		topics.add(topic);
	}

	public void addTopic(int index, Topic topic) {
		if (index < topics.size() && index > 0)
			topics.add(index, topic);
	}

	@JsonProperty("content")
	public ArrayList<Topic> getTopics() {
		return topics;
	}

	@JsonProperty("content")
	public void setTopics(ArrayList<Topic> topics) {
		this.topics = topics;
	}

	@JsonProperty("meeting")
	public String getAttachedMeetingID() {
		return attachedMeetingID;
	}

	@JsonProperty("meeting")
	public void setAttachedMeetingID(String attachedMeetingID) {
		this.attachedMeetingID = attachedMeetingID;
	}

	public void pprint() {
		System.out.println(String.format("%s (%d)", getTitle(), getTopics()
				.size()));
		for (Iterator<Topic> i = topics.iterator(); i.hasNext();) {
			pprint(i.next(), 0);
		}
	}
	
	protected void pprint(Topic s, int depth) {
		final Topic root = s;
		final ArrayList<Topic> topicList = root.getTopics();
		for (int i = -1; i < depth; i++) {
			System.out.print("--");
		}
		System.out.println(String.format("%s (%d)", root.getTitle(),
				topicList.size()));
		for (Iterator<Topic> i = topicList.iterator(); i.hasNext();) {
			pprint(i.next(), depth + 1);
		}
	}
	
	@JsonIgnore
	public int getDepth() {
		int depth = 0;
		for (Iterator<Topic> i = topics.iterator(); i.hasNext();) {
			depth += depthHelper(i.next(), 0);
		}
		return depth;
	}
	
	@JsonIgnore
	private int depthHelper(Topic t, int depth) {
		int subDepth = 0;
		final Topic root = t;
		final ArrayList<Topic> topicList = root.getTopics();
		for (Iterator<Topic> i = topicList.iterator(); i.hasNext();) {
			subDepth = depthHelper(i.next(), depth + 1);
		}
		return subDepth;
	}

	@Override
	public String toJSON() throws JsonGenerationException, IOException {
		ByteArrayOutputStream _json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(_json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = new JsonFactory().createGenerator(ps,
				JsonEncoding.UTF8);
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		// Build JSON Object
		jgen.writeStartObject();
		jgen.writeStringField("agendaID", getID());
		jgen.writeStringField("title", getTitle());
		jgen.writeArrayFieldStart("content");
		mapper.writeValue(jgen, getTopics());
		jgen.writeEndArray();
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String json = _json.toString("UTF8");
//		ps.close();
		return json;
		
//		return mapper.writeValueAsString(this);
		
	}
}
