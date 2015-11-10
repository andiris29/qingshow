//
//  NSFileManager+QSExtension.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/7.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSFileManager(QSExtension)
- (long long) fileSizeAtPath:(NSString*)filePath;
- (long long )folderSizeAtPath:(NSString*)folderPath;
@end
