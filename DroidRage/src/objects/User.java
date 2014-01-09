package objects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "userID", "displayName", "email", "phone", "company",
		"title", "location" })
public class User extends SimpleUser {
	private String email;
	private String phone;
	private String company;
	private String title;
	private String location;

	public User() {
		// required empty constructor
	}

	public User(int userID) {
		super.setUserID(userID);
	}

	public User(User copyUser) {
		super.setUserID((copyUser.getUserID() != null ? copyUser.getUserID()
				: ""+0));
		super.setDisplayName(copyUser.getDisplayName());
		setEmail(copyUser.getEmail());
		setPhone(copyUser.getPhone());
		setCompany(copyUser.getCompany());
		setTitle(copyUser.getTitle());
		setLocation(getLocation());
	}

	/* Required Fields */

	@Override
	public String getUserID() {
		return super.getUserID();
	}

	@Override
	public void setUserID(String id) {
		super.setUserID(id);
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		super.setDisplayName(displayName);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/* Optional fields */

	public String getPhone() {
		return (phone != null && !phone.isEmpty()) ? phone : "";
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompany() {
		return (company != null && !company.isEmpty()) ? company : "";
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getTitle() {
		return (title != null && !title.isEmpty()) ? title : "";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return (location != null && !location.isEmpty()) ? location : "";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("***** User Details *****\n");
		builder.append("getUserID()\t");
		builder.append(getUserID() + "\n");
		builder.append("getDisplayName()\t");
		builder.append(getDisplayName() + "\n");
		builder.append("getEmail()\t");
		builder.append(getEmail() + "\n");
		builder.append("getPhone()\t");
		builder.append(getPhone() + "\n");
		builder.append("getCompany()\t");
		builder.append(getCompany() + "\n");
		builder.append("getTitle()\t");
		builder.append(getTitle() + "\n");
		builder.append("getLocation()\t");
		builder.append(getLocation() + "\n");
		builder.append("************************");
		return builder.toString();
	}

	public SimpleUser toSimpleUser() {
		SimpleUser simple = new SimpleUser();
		simple.setUserID(this.userID);
		simple.setDisplayName(this.displayName);
		return simple;
	}

	public static String toJSON(User user) throws JsonGenerationException,
			IOException {
		ByteArrayOutputStream _json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(_json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = new JsonFactory().createGenerator(ps,
				JsonEncoding.UTF8);

		// Build JSON Object
		jgen.writeStartObject();
		jgen.writeStringField("userID", user.getUserID());
		jgen.writeStringField("displayName", user.getDisplayName());
		jgen.writeStringField("email", user.getEmail());
		jgen.writeStringField("phone", user.getPhone());
		jgen.writeStringField("company", user.getCompany());
		jgen.writeStringField("title", user.getTitle());
		jgen.writeStringField("location", user.getLocation());
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String json = _json.toString("UTF8");
		ps.close();
		return json;
	}
}
