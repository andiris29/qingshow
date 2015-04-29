//
//  QSTopicTableViewProvider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"



@protocol QSTopicTableViewProviderDelegate <NSObject>

- (void)didClickTopic:(NSDictionary*)topicDict;
- (void)didClick_1Topic:(NSDictionary *)topicDict;

@end

@interface QSTopicTableViewProvider : QSTableViewBasicProvider

@property (weak, nonatomic) NSObject<QSTopicTableViewProviderDelegate>* delegate;

@end
