<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");


/**
 *        Create a graphDb connection 
 */
$client= new Client();

        //get the index
        $meetingIndex = new Index\NodeIndex($client, 'Meetings');
        $meetingIndex->save();

if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST') == 0){
        //create meeting POST
        //get the json string post content
        $postContent = json_decode(@file_get_contents('php://input'));
        
        //create the node
        $meetingNode= $client->makeNode();
        
        //sets the property on the node
        $meetingNode->setProperty('userID', $postContent->userID)
                ->setProperty('title', $postContent->title)
                ->setProperty('datetime', $postContent->datetime)
                ->setProperty('endDatetime', $postContent->endDatetime)
                ->setProperty('description',$postContent->description)
                ->setProperty('location', $postContent->location)
				->setProperty('nodeType','Meeting');
        
        //actually add the node in the db
        $meetingNode->save();
        
        //create a relation to the user who made the meeting
        $user = $client->getNode($postContent->userID);
        $array = $user->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'User')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$postContent->userID.' is an not a user node.'));
				return 1;
			}
		} 
        $meetingRel = $user->relateTo($meetingNode, 'MADE_MEETING')
                ->save();
        
        $attendeeArray = $postContent->attendance;
        foreach($attendeeArray as $attendee){
                $user = $client->getNode($attendee->userID);
                $array = $user->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'User')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$attendee->userID.' is an not a user node.'));
				return 1;
			}
		} 
                $attRel = $user->relateTo($meetingNode, 'ATTEND_MEETING')->save();//->setProperty('ATTENDANCE', $attendee->{$key}[0])
        }
        
        //add the index        
        $response= $meetingIndex->add($meetingNode, 'ID', $meetingNode->getID());
        
        //return the created meeting id
        $array=array();
        $array['meetingID']=$meetingNode->getId();
        echo json_encode($array);
        
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0){
        //getMeetingInfo
        $meetingNode=$client->getNode($_GET['id']);
                $array = $meetingNode->getProperties();
				if(array_key_exists('nodeType', $array)){
					if(strcasecmp($array['nodeType'], 'Meeting')!=0){
						echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a meeting node.'));
						return 1;
					}
				}            
                if ($meetingNode != null) {
                
                        $outputArray=$meetingNode->getProperties();
                
                        //get relationships
                        //Arrays are three levels deep
                        // 1-outputArray: overall output array 
                        // 2-userOutputArray: all related users
                        // 3-uArray: individual users
                        $relationArray = $meetingNode->getRelationships(array(), Relationship::DirectionIn);
                        $userOutputArray = array();
                        $u=0;
                        foreach($relationArray as $rel){
                                $relType = $rel->getType();
                                if($relType == 'ATTEND_MEETING') {
                                        $userNode=$rel->getStartNode();
                                        $uArray = array();
                                        $uArray['userID']=$userNode->getId();
                                        $userOutputArray[$u++] = $uArray;
                                } 
                        }
                        $outputArray['attendance'] = $userOutputArray;
                        
                        echo json_encode($outputArray);
                } else {
                        echo "Node not found";
                }
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT') == 0){
        //updateMeeting
        
        //get the json string post content
        $postContent = json_decode(@file_get_contents('php://input'));
        
        $meeting=$client->getNode($postContent->meetingID);
        if(sizeof($meeting > 0)){
		$array = $meeting->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'Meeting')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$postContent->meetingID.' is an not a meeting node.'));
				return 1;
			}
		}
                if(strcasecmp($postContent->field, 'user') ==0){
                        $meeting->setProperty('user', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
						unset($array['nodeType']);
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'title') ==0){
                        $meeting->setProperty('title', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
						unset($array['nodeType']);
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'datetime') ==0){
                        $meeting->setProperty('datetime', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
						unset($array['nodeType']);
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'duration') ==0){
                        $meeting->setProperty('duration', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
						unset($array['nodeType']);
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'location') ==0){
                        $meeting->setProperty('location', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
						unset($array['nodeType']);
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'description') ==0){
                        $meeting->setProperty('description', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
						unset($array['nodeType']);
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'attendance') ==0){
                        $attendeeArray = $postContent->value;
                        foreach($attendeeArray as $attendee){
                                $key = key(get_object_vars($attendee));
                                $users = $userIndex->query('name:'.$key[0]);
                                $meetingRels = $meeting->getRelationships('ATTEND_MEETING');
                                foreach($meetingRels as $rel){
                                        if ($users[0]->getID() == $rel->getStartNode->getID()){
                                                $rel->setProperty('ATTENDANCE', $attendee->{$key}[0])
                                                        ->save();
                                        }else{
                                                $attendRel = $users[0]->relateTo($meeting)
                                                        ->setProperty('ATTENDANCE', $attendee->{$key}[0])
                                                        ->save();
                                        }
                                }
                        }
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
						unset($array['nodeType']);
                        echo json_encode($array);
                }
        }
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE') == 0){      
        //delete meeting DELETE
        //get the id
        $id=$_GET['id'];
        
        //get the node
        $node = $client->getNode($id);
        //make sure the node exists
        if($node != NULL){
                //check if node has meeting index
                $meeting = $meetingIndex->findOne('ID', ''.$id);
                $array = $meeting->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'Meeting')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a meeting node.'));
				return 1;
			}
		}                                
                //only delete the node if it's a note
                if($meeting != NULL){
                        //get the relationships
                        $relations = $meeting->getRelationships();
                        foreach($relations as $rel){
                                //remove all relationships
                                $rel->delete();
                        }
                        
                        //delete node and return true
                        $meeting->delete();
                        $array = array('valid'=>'true');
                        echo json_encode($array);
                } else {
                        //return an error otherwise
                        $errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a meeting node');
                        echo json_encode($errorarray);
                }
        } else {
                //return an error if ID doesn't point to a node
                $errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
                echo json_encode($errorarray);
        }
}else{
        echo $_SERVER['REQUEST_METHOD'] ." request method not found in Meeting";
}
?>
