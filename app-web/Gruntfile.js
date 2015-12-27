module.exports = function(grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        clean : ['target'],
        requirejs: {
            compile: {
                options: {
                    baseUrl: "src/main",
                    name: "js/app",
                    paths : {
                        qs : 'js'
                    },
                    include: [
                        "qs/views/P01NotFound",
                        "qs/views/P02ShareShow",
                        "qs/views/P03ShareTrade",
                        "qs/views/P04ShareBonus",
                        "qs/views/P05ShareItems"
                    ],
                    out: "target/js/qingshow-aio.js"
                }
            }
        },
        copy: {
            main: {
                files: [{
                    expand: true,
                    cwd:'src/main/',
                    src: [
                        'assets/**',
                        'bower_components/**',
                        'css/**',
                        'libs/**',
                        'libs-andrea/**',
                        '*.html',
                        'js/views/*.html'
                    ],
                    dest: 'target'},

                ]
            }
        }

    });

    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-requirejs');
    grunt.loadNpmTasks('grunt-contrib-copy');

    grunt.registerTask('build', ['requirejs', 'copy']);
    grunt.registerTask('default', ['clean', 'build']);


}