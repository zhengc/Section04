//
//  iWinAddUsersViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/25/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface iWinAddUsersViewController : UIViewController
@property (weak, nonatomic) IBOutlet UITableView *userListTableView;
@property (weak, nonatomic) IBOutlet UITextField *emailField;
- (IBAction)onClickSendInvite;
- (IBAction)onClickSave;
- (IBAction)onClickCancel;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withPageName:(NSString *)pageName inEditMode:(BOOL)isEditing;
@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UIButton *saveButton;
@property (weak, nonatomic) IBOutlet UIButton *sendInviteButton;
@end