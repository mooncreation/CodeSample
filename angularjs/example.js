  app.controller('BookmarkController', function($scope) {
    var bookmarkList;
    bookmarkList = [
      {
        id: 1,
        url: "html://www.tenflares.com",
        title: "Tenflares",
        description: "Lorem ipsum dolor sit amet, conse erty fdsfds fdsfdsfdsfds ctetur adipisicing elit, sed do eiusmod  tempor incididunt ut labore et dolore magna",
        loves: 15,
        loved: 1
      }, {
        id: 2,
        url: "html://www.groupmarks.com",
        title: "Groupmarks",
        description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit, Lorem ipsum dolor sit amet, consectetur adipisicing elit, Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna Lorem ipsum dolor sit",
        loves: 15,
        loved: 0
      }, {
        id: 3,
        url: "html://www.google.com",
        title: "Google",
        description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt t abore et dolore magna dolore eu fugiat nulla pariatur.",
        loves: 23,
        loved: 0
      }, {
        id: 4,
        url: "html://www.tenflares.com",
        title: "Tenflares",
        description: "Lorem ipsum dolor sit amet, conse erty fdsfds fdsfdsfdsfds ctetur adipisicing elit, sed do eiusmod  tempor incididunt ut labore et dolore magna",
        loves: 15,
        loved: 0
      }, {
        id: 5,
        url: "html://www.groupmarks.com",
        title: "Groupmarks",
        description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit, Lorem ipsum dolor sit amet, consectetur adipisicing elit, Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna Lorem ipsum dolor sit",
        loves: 75,
        loved: 0
      }, {
        id: 6,
        url: "html://www.google.com",
        title: "Google",
        description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt t abore et dolore magna dolore eu fugiat nulla pariatur.",
        loves: 76,
        loved: 1
      }, {
        id: 7,
        url: "html://www.tenflares.com",
        title: "Tenflares",
        description: "Lorem ipsum dolor sit amet, conse erty fdsfds fdsfdsfdsfds ctetur adipisicing elit, sed do eiusmod  tempor incididunt ut labore et dolore magna",
        loves: 15,
        loved: 0
      }, {
        id: 8,
        url: "html://www.groupmarks.com",
        title: "Groupmarks",
        description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit, Lorem ipsum dolor sit amet, consectetur adipisicing elit, Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna Lorem ipsum dolor sit",
        loves: 25,
        loved: 1
      }, {
        id: 9,
        url: "html://www.google.com",
        title: "Google",
        description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt t abore et dolore magna dolore eu fugiat nulla pariatur.",
        loves: 765,
        loved: 0
      }, {
        id: 10,
        url: "html://www.tenflares.com",
        title: "Tenflares",
        description: "Lorem ipsum dolor sit amet, conse erty fdsfds fdsfdsfdsfds ctetur adipisicing elit, sed do eiusmod  tempor incididunt ut labore et dolore magna",
        loves: 15,
        loved: 0
      }, {
        id: 11,
        url: "html://www.groupmarks.com",
        title: "Groupmarks",
        description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit, Lorem ipsum dolor sit amet, consectetur adipisicing elit, Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna Lorem ipsum dolor sit",
        loves: 1555,
        loved: 0
      }, {
        id: 12,
        url: "html://www.google.com",
        title: "Google",
        description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt t abore et dolore magna dolore eu fugiat nulla pariatur.",
        loves: 43,
        loved: 0
      }, {
        id: 13,
        url: "html://www.tenflares.com",
        title: "Tenflares",
        description: "Lorem ipsum dolor sit amet, conse erty fdsfds fdsfdsfdsfds ctetur adipisicing elit, sed do eiusmod  tempor incididunt ut labore et dolore magna",
        loves: 98,
        loved: 0
      }, {
        id: 14,
        url: "html://www.groupmarks.com",
        title: "Groupmarks",
        description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit, Lorem ipsum dolor sit amet, consectetur adipisicing elit, Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna Lorem ipsum dolor sit",
        loves: 5,
        loved: 1
      }, {
        id: 15,
        url: "html://www.google.com",
        title: "Google",
        description: "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt t abore et dolore magna dolore eu fugiat nulla pariatur.",
        loves: 90,
        loved: 0
      }
    ];
    $scope.bookmarks = bookmarkList;
    $scope.groups = [
      {
        url: "html://www.tenflares.com",
        title: "Group 1"
      }, {
        url: "html://www.groupmarks.com",
        title: "Group 2"
      }, {
        url: "html://www.google.com",
        title: "Group 3"
      }
    ];
    $scope.categories = [
      {
        id: "1",
		url: "/templates/bookmarks/no-bookmark.html",
        name: "No Bookmark"
      }, {
        id: "2",
		url: "/templates/bookmarks/no-comments.html",
        name: "No Comments"
      }, {
        id: "3",
        name: "Category 3"
      }
    ];
    $scope.sortableOptions = {
      stop: function(e, ui) {
        var logEntry;
        logEntry = bookmarkList.map(function(i) {
          return i.id;
        });
      }
    };
    $scope.sortableCategories = {
      stop: function(e, ui) {
        var logEntry;
        logEntry = categoryList.map(function(i) {
          return i.id;
        });
      }
    };
    $scope.bookmarkToggleSearchBox = function() {
      if ($scope.searchExpanded === "expanded") {
        $scope.searchExpanded = "";
      } else {
        $scope.searchExpanded = "expanded";
      }
    };
  });