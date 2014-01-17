//
//  iWinViewAndChangeSettingsViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/4/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewAndChangeSettingsViewController.h"
#import "iWinAppDelegate.h"
#import "Contact.h"
#import "Settings.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinViewAndChangeSettingsViewController ()
@property (nonatomic) BOOL isEditing;
@property (nonatomic) Contact *contact;
@property (nonatomic) Settings *settings;
@property (nonatomic) NSInteger userID;
@property (nonatomic) NSArray *options;
@property (nonatomic) NSNumber *tableIndex;
@property (nonatomic)NSManagedObjectContext *context;
@property (nonatomic) UIAlertView *deleteAlertView;
@end


@implementation iWinViewAndChangeSettingsViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID:(NSInteger) userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.userID = userID;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //self.cancelButton.hidden = YES;
    [self showFields:NO];
    [self enableInteraction:NO];
    self.options = [[NSArray alloc] initWithObjects:@"At time of event",@"5 minutes before",@"15 minutes before", @"30 minutes before", @"1 hour before", @"2 hours before", @"1 day before",@"2 days before", nil];
    
    //
    //
    //
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    self.context = [appDelegate managedObjectContext];
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Contact" inManagedObjectContext:self.context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userID = 1"];
    [request setPredicate:predicate];
    
    NSError *error;
    NSArray *result = [self.context executeFetchRequest:request
                                             error:&error];
    self.contact = (Contact*)[result objectAtIndex:0];
    
    
    
    NSEntityDescription *entityDesc1 = [NSEntityDescription entityForName:@"Settings" inManagedObjectContext:self.context];
    
    NSFetchRequest *request1 = [[NSFetchRequest alloc] init];
    [request1 setEntity:entityDesc1];
    
    NSPredicate *predicate1 = [NSPredicate predicateWithFormat:@"userID = 1"];
    [request1 setPredicate:predicate1];
    
    NSError *error1;
    NSArray *result1 = [self.context executeFetchRequest:request1
                                              error:&error1];
    self.settings = (Settings*)[result1 objectAtIndex:0];
    //
    //
    //
    
    
    self.emailTextField.text = self.contact.email;
    [self.shouldNotifySwitch setOn:[self.settings.shouldNotify boolValue]];
    
}

-(IBAction)changeSwitch:(id)sender
{
    //Don't think this method is worthwhile
    if([self.shouldNotifySwitch isOn]){
        self.settings.shouldNotify = [NSNumber numberWithInt:1];
    } else {
        self.settings.shouldNotify = [NSNumber numberWithInt:0];
    }
    [self saveChanges];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.options count];
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"PickerCell"];
    cell.textLabel.text = [self.options objectAtIndex:indexPath.row];
    if ([[NSNumber numberWithInt:indexPath.row] isEqual:self.settings.whenToNotify])
    {
        [cell setAccessoryType:UITableViewCellAccessoryCheckmark];
    }
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    for (int i=0; i<[self.options count]; i++)
    {
        UITableViewCell *cell = [tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:i inSection:0]];
        if (i == indexPath.row)
        {
            [cell setAccessoryType:UITableViewCellAccessoryCheckmark];
            self.tableIndex = [NSNumber numberWithInt:i];
        }
        else
        {
            [cell setAccessoryType:UITableViewCellAccessoryNone];
        }
    }
    [self saveChanges];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction)onCancel:(id)sender
{
    //CLOSE PAGE
    
    [self.saveAndEditButton setTintColor:[UIColor blueColor]];
    [self showFields:NO];
    [self clearFields];
    [self enableInteraction:NO];
    self.isEditing = NO;
    self.oldPasswordTextField.text = @"********";
    [self.saveAndEditButton setTitle:@"Change Email/Password" forState:UIControlStateNormal];
    
    //Pull Email from DB
}

-(IBAction)onDelete:(id)sender
{
    self.deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this contact?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
    [self.deleteAlertView show];
}


-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
            //Perform deletion
    }
   
}

-(IBAction)onEdit:(id)sender
{
    
        self.isEditing = YES;
        [self showFields:YES];
        [self clearFields];
        self.saveAndEditButton.hidden = YES;
        [self enableInteraction:YES];
}

- (IBAction)onSaveSwitch:(id)sender
{
    //Save should notify switch
    if([self.shouldNotifySwitch isOn]){
        self.settings.shouldNotify = [NSNumber numberWithInt:1];
    } else {
        self.settings.shouldNotify = [NSNumber numberWithInt:0];
    }
    
    //Save when to notify table
    self.settings.whenToNotify = self.tableIndex;
    
    //Save the rest
    if ([self.oldPasswordTextField.text isEqual:self.settings.password] && [self.passwordTextField.text isEqual:self.confirmPasswordTextField.text]) {
        [self showFields:NO];
        [self.saveAndEditButton setTitle:@"Change Email/Password" forState:UIControlStateNormal];
        [self.saveAndEditButton setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        [self saveChanges];
        [self enableInteraction:NO];
        self.isEditing = NO;
        self.saveAndEditButton.hidden = NO;
    } else {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Old password or confirmation password did not match." delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
        [self clearFields];
    }
    
    
}
- (IBAction)onSaveTable:(id)sender
{
    self.settings.whenToNotify = self.tableIndex;
}

-(void) saveChanges
{
    //Push new password and email to DB only if old password matches.
    //You must enter old password for any change to take affect.
    self.contact.email = self.emailTextField.text;
    self.settings.email = self.emailTextField.text;
    if ([self.confirmPasswordTextField.text isEqual: @""]){
        //Don't change the password because no new one was entered
    }else{
    self.settings.password = self.confirmPasswordTextField.text;
    }
    NSError *error;
    [self.context save:&error];
}

-(void) saveNotificationsChangesToDB
{
    
}

-(void) saveEmailOrPasswordChangesToDB
{
    
}


-(void) showFields: (BOOL) show
{
    if(show) {
        self.oldPasswordTextField.text = @"";
        self.oldPasswordLabel.text = @"Old Password:";
        self.confirmPasswordTextField.hidden = NO;
        self.passwordTextField.hidden = NO;
        self.confirmPasswordLabel.hidden = NO;
        self.oldPasswordLabel.hidden = NO;
        self.passwordLabel.hidden = NO;
        
    } else{
        self.oldPasswordTextField.text = @"********";
        self.oldPasswordLabel.text = @"Password:";
        self.confirmPasswordTextField.hidden = YES;
        self.passwordTextField.hidden = YES;
        self.confirmPasswordLabel.hidden = YES;
        self.passwordLabel.hidden = YES;
    }
    
}

-(void) enableInteraction: (BOOL) enable
{
    if (enable){
        self.emailTextField.userInteractionEnabled = YES;
        self.confirmPasswordTextField.userInteractionEnabled = YES;
        self.passwordTextField.userInteractionEnabled = YES;
        self.oldPasswordTextField.userInteractionEnabled = YES;
        [self.emailTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.oldPasswordTextField setBorderStyle:UITextBorderStyleRoundedRect];
    }else{
        self.emailTextField.userInteractionEnabled = NO;
        self.confirmPasswordTextField.userInteractionEnabled = NO;
        self.passwordTextField.userInteractionEnabled = NO;
        self.oldPasswordTextField.userInteractionEnabled = NO;
        [self.emailTextField setBorderStyle:UITextBorderStyleNone];
        [self.oldPasswordTextField setBorderStyle:UITextBorderStyleNone];


    }
}

- (void) clearFields
{
    self.oldPasswordTextField.text = @"";
    self.passwordTextField.text = @"";
    self.confirmPasswordTextField.text = @"";
}

@end
