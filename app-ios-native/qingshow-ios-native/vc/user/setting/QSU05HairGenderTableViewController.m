//
//  QSU05HairGenderTableViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/23.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU05HairGenderTableViewController.h"
#import "UIViewController+ShowHud.h"

@interface QSU05HairGenderTableViewController ()

@end

@implementation QSU05HairGenderTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Initialize Navigation
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

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    return [self.codeTable count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString* identifier = @"code";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(15, 12, 50, 20)];
        label.tag = 1000;
        label.text = self.codeTable[indexPath.row];
        [cell.contentView addSubview:label];
    } else {
        UILabel *label = (UILabel *)[cell viewWithTag:1000];
        label.text = self.codeTable[indexPath.row];
    }
    
    [self configureCheckmarkForCell:cell atIndex:indexPath];
    return cell;
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the item to be re-orderable.
    return NO;
}

#pragma mark - Table view delegate

// In a xib-based application, navigation from a table can be handled in -tableView:didSelectRowAtIndexPath:
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    if (cell.accessoryType == UITableViewCellAccessoryNone) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    } else {
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
}

#pragma mark - Action
- (void) actionSave {
    NSMutableArray *selected = [[NSMutableArray alloc]init];
    for (int i = 0; i < self.codeTable.count; i++) {
        NSIndexPath *indexPaths = [NSIndexPath indexPathForRow:i inSection:0];
        UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:indexPaths];
        if (cell.accessoryType == UITableViewCellAccessoryCheckmark) {
            [selected addObject:[NSNumber numberWithInt:i]];
        }
    }
    
    if (selected.count == 0) {
        [self showErrorHudWithText:@"内容没有选择"];
        return;
    }
    
    [self.delegate codeUpdateViewController:self forCodeType:self.codeType bySelectedCode:selected];
}

#pragma mark - private
- (void)configureCheckmarkForCell:(UITableViewCell *)cell atIndex:(NSIndexPath *)indexPath {
    NSNumber *row = [NSNumber numberWithInteger:indexPath.row];
    if (self.selectCodes == nil) {
        cell.accessoryType = UITableViewCellAccessoryNone;
    } else if (![self.selectCodes containsObject:row]) {
        cell.accessoryType = UITableViewCellAccessoryNone;
    } else {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    }
}

@end
