//
//  QSModelListTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSModelListTableViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import "UIImageView+MKNetworkKitAdditions.h"
#import "ServerPath.h"

@implementation QSModelListTableViewCell

#pragma mark - Life Cycle
- (void)awakeFromNib
{
    // Initialization code
}


#pragma mark - Table View Cell
- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark - IBAction
- (IBAction)followButtonPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(favorBtnPressed:)]) {
        [self.delegate favorBtnPressed:self];
    }
}

#pragma mark - Binding
- (void)bindWithPeople:(NSDictionary*)modelDict
{
    self.nameLabel.text = modelDict[@"name"];
    self.detailLabel.text = [NSString stringWithFormat:@"%@cm %@kg",modelDict[@"height"], modelDict[@"weight"]];
    NSString* headPhotoPath = [NSString stringWithFormat:@"%@%@",kImageUrlBase, modelDict[@"portrait"]];
    [self.headPhotoImageView setImageFromURL:[NSURL URLWithString:headPhotoPath]];
#warning 缺ShowNumber, FavorNumber
}

@end
