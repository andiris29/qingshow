//
//  QSU02UserSettingViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/8.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU02UserSettingViewController.h"

@interface QSU02UserSettingViewController ()
@property (weak, nonatomic) IBOutlet UILabel *birthdayLabel;
@property (strong, nonatomic) IBOutlet UITableView *settingTableView;
@end

@implementation QSU02UserSettingViewController

NSDate *_birthday;
bool _datePickerVisable;

- (id)init
{
//    self = [self initWithNibName:@"QSU02UserSettingViewController" bundle:nil];
//    if (self) {
//    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    
    [self initNavigation];
    _datePickerVisable = NO;
    [self loadUserSetting];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 1 && indexPath.row == 2) {
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"DatePickerCell"];
        
        if (cell == nil) {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"DatePickerCell"];
            
            UIDatePicker *datePicker = [[UIDatePicker alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 320.0f, 216.0f)];
            
            datePicker.tag = 100;
            [cell.contentView addSubview:datePicker];
            
            [datePicker addTarget:self action:@selector(dateChanged) forControlEvents:UIControlEventValueChanged];
        }
        return cell;
    } else {
        return [super tableView:tableView cellForRowAtIndexPath:indexPath];
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 1 && _datePickerVisable) {
        return 7;
    } else {
        return [super tableView:tableView numberOfRowsInSection:section];
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 1 && indexPath.row == 3) {
        return 217.0f;
    } else {
        return [super tableView:tableView heightForRowAtIndexPath:indexPath];
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    if (indexPath.section == 1 && indexPath.row == 2) {
        [self showDatePicker];
    }
}

#pragma mark - Private
- (void)initNavigation {
    NSLog(@"initNavigation");
    self.navigationItem.title = @"设置";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    UIBarButtonItem *btnSave = [[UIBarButtonItem alloc]initWithTitle:@"保存"
                                                               style:UIBarButtonItemStylePlain
                                                              target:self
                                                              action:@selector(actionSave)];
    
    [[self navigationItem] setRightBarButtonItem:btnSave];
}

- (void)loadUserSetting {
    _birthday = [NSDate date];
    [self updateBirthDayLabel];
}


- (void) updateBirthDayLabel {
    NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
    [formatter setDateFormat:@"yyyy/MM/dd"];
    self.birthdayLabel.text = [formatter stringFromDate:_birthday];
}

- (void) showDatePicker {
    _datePickerVisable = YES;
    NSIndexPath *indexPathDatePicker = [NSIndexPath indexPathForRow:3 inSection:1];
    
    [self.tableView insertRowsAtIndexPaths:@[indexPathDatePicker]
                          withRowAnimation:UITableViewRowAnimationAutomatic];
}

#pragma mark - Action
- (void) actionSave {
    
}

- (void) dateChanged {
    
}

@end
