//
//  iWinViewAndAddNotesViewController.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 10/29/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinMergeNoteViewController.h"

@protocol ViewAddNoteDelegate <NSObject>

-(void)saveNoteClicked;
-(void)cancelNoteClicked;
-(void)mergeNoteClicked;
@end


@interface iWinViewAndAddNotesViewController : UIViewController <MergeNoteDelegate>
@property (nonatomic) id<ViewAddNoteDelegate> addNoteDelegate;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing;
- (IBAction)saveButton:(id)sender;
- (IBAction)cancelButton:(id)sender;
- (IBAction)mergeNotesButton:(id)sender;

@end
