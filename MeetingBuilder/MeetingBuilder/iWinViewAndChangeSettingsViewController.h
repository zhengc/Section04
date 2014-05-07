//
//  iWinViewAndChangeSettingsViewController.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/4/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SettingsDelegate <NSObject>

-(void)onDeleteAccount;
@end

@interface iWinViewAndChangeSettingsViewController : UIViewController <UITableViewDelegate,UITableViewDataSource, UIAlertViewDelegate>

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID:(NSInteger) userID;

- (IBAction)onCancel:(id)sender;
- (IBAction)onEdit:(id)sender;
- (IBAction)onDelete:(id)sender;
- (IBAction)onSave:(id)sender;

@property (weak, nonatomic) IBOutlet UITextField *emailTextField;
@property (weak, nonatomic) IBOutlet UITextField *oldPasswordTextField;
@property (weak, nonatomic) IBOutlet UITextField *passwordTextField;
@property (weak, nonatomic) IBOutlet UITextField *confirmPasswordTextField;

@property (weak, nonatomic) IBOutlet UITableView *whenToNotifyPicker;
@property (weak, nonatomic) IBOutlet UISwitch *shouldNotifySwitch;

@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UIButton *saveAndEditButton;
@property (weak, nonatomic) IBOutlet UIButton *deleteButton;
@property (weak, nonatomic) IBOutlet UIButton *saveSwitch;

@property (weak, nonatomic) IBOutlet UILabel *confirmPasswordLabel;
@property (weak, nonatomic) IBOutlet UILabel *passwordLabel;
@property (weak, nonatomic) IBOutlet UILabel *oldPasswordLabel;

@property (nonatomic) id<SettingsDelegate> settingsDelegate;


@end
