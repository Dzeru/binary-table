package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.dao.GroupGoalDAO;
import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.dto.CollaboratorsList;
import com.pie.binarytable.dto.GoalId;
import com.pie.binarytable.dto.UploadScreenshotUrl;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.GroupGoal;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Random;

import static org.apache.http.entity.ContentType.MULTIPART_FORM_DATA;

@RestController
public class RestGoalsController
{
	@Autowired
	GoalDAO goalDAO;

	@Autowired
	GroupGoalDAO groupGoalDAO;

	@Autowired
	UserDAO userDAO;

	/*
	Sends JSON with Goal object to JavaScript functions in /goal
    */
	@RequestMapping(value = "/getgoal", method = RequestMethod.GET)
	public Goal goal(@RequestParam(value="id") Long goalId)
	{
		return goalDAO.findByIdEquals(goalId);
	}

	/*
	Updates @Param currentState of goal from /goal page
	 */
	@RequestMapping(value = "/updategoal", method = RequestMethod.POST)
	public ResponseEntity updateGoal(@RequestBody Goal goal)
	{
		boolean result = false; //error by default
		goalDAO.save(goal);

		if(goalDAO.findByIdEquals(goal.getId()).getCurrentState().equals(goal.getCurrentState()))
		{
			result = true; //success
			return ResponseEntity.ok().body(result);
		}
		else return ResponseEntity.badRequest().body(result);
	}

	/*
	Deletes goal by goal id
	 */
	@RequestMapping(value = "/deletegoal", method = RequestMethod.POST)
	public ResponseEntity deleteGoal(@RequestBody GoalId goalId)
	{
		boolean result = false; //error by default
		Long id = goalId.getId();

		if(goalDAO.findByIdEquals(id).isGroupGoal())
		{
			groupGoalDAO.deleteByGoalId(id);

			if(!groupGoalDAO.findByGoalId(id).isEmpty()) //list!
			{
				return ResponseEntity.badRequest().body(result);
			}
		}

		goalDAO.deleteById(id);

		if(goalDAO.findByIdEquals(id) == null) //one object!
		{
			result = true; //success
			return ResponseEntity.ok().body(result);
		}
		else return ResponseEntity.badRequest().body(result);
	}

	@RequestMapping(value = "/getcollaborators", method = RequestMethod.GET)
	public CollaboratorsList getCollaborators(@RequestParam(value = "id") Long id)
	{
		ArrayList<GroupGoal> groupGoals = groupGoalDAO.findByGoalId(id);
		CollaboratorsList collaboratorsList = new CollaboratorsList();

		for(GroupGoal g : groupGoals)
		{
			collaboratorsList.addCollaborator(userDAO.findByIdEquals(g.getUserId()).getUsername());
		}
		return collaboratorsList;
	}

	/*
	https://ru.stackoverflow.com/questions/488289/Пустой-photo-при-upload-фотографии-через-vk-api
	*/
	@RequestMapping(value = "/vkpostimage", method = RequestMethod.POST)
	public ResponseEntity sendScreenshot(@RequestBody UploadScreenshotUrl uploadScreenshotUrl) throws Exception
	{
		HttpClientBuilder builder = HttpClientBuilder.create();
		CloseableHttpClient httpClient = builder.build();

		HttpPost httpPost = new HttpPost(uploadScreenshotUrl.getUploadUrl());
		URL url;

		try
		{
			url = new URL(uploadScreenshotUrl.getImageUrl());
			String filename = new Random().nextLong() + ".jpg";

			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream(filename);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();

			File file = new File(filename);

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.addBinaryBody("file", file, MULTIPART_FORM_DATA, filename);
			final HttpEntity entity = entityBuilder.build();
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
			BufferedReader in = new BufferedReader(reader);

			StringBuffer response2 = new StringBuffer();
			String inputLine;

			while((inputLine = in.readLine()) != null)
			{
				response2.append(inputLine);
			}
			reader.close();

			file.delete();

			if(response2.length() == 0) return ResponseEntity.badRequest().body("Can not save screenshot!");
			return ResponseEntity.ok().body(response2.toString());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return ResponseEntity.badRequest().body("Can not save screenshot!");
	}
}
