(function () {

	angular.module('invoice', ['ngRoute'])
	.config(function ($routeProvider) {
		$routeProvider.when('/', {
			templateUrl: '/templates/pages/invoice.html',
			controller: 'invoiceListController'
		})
		.when('/invoice/new', {
			templateUrl: '/templates/pages/invoice-create.html',
			controller: 'invoiceNewController'
		})
		.when('/customers', {
			templateUrl: '/templates/pages/customer-list.html',
			controller: 'customerListController'
		})
		.when('/customer/:id', {
			templateUrl: '/templates/pages/customer-edit.html',
			controller: 'customerEditController'
		})
		.when('/products', {
			templateUrl: '/templates/pages/products-list.html',
			controller: 'productsListController'
		})
		.when('/products/:id', {
			templateUrl: '/templates/pages/product-edit.html',
			controller: 'productsEditController'
		})
		.otherwise({
			redirectTo: '/'
		});
	})
	.controller('invoiceListController', function ($http) {
		var controller = this;
		$http.get('/api/invoices').success(function (data) {
			controller.invoices = data;
		});

	})
	.controller('customerListController',['$http','$scope',function($http, $scope){
		var controller = this;
		$http.get('/api/customers').success(function (data) {
			controller.customers = data;
		});
	}])
	.controller('customerEditController',['$http', '$scope','$routeParams','$location', function ($http, $scope,$routeParams,$location) {
		var controller = this;
		$http.get('/api/customers/' + $routeParams.id).success(function (data) {
			
			controller.customer = data;
			$scope.customer = data;
		});

		controller.updateCustomer = function(customer){
			console.log(customer);
			$http.put('/api/customers/' + $routeParams.id,{'name':customer.name,'address':customer.address,'phone':customer.phone}).success(function(data){
				$scope.customer = data;
				$location.path('/customers');
			});
		};

	}])
	.controller('productsListController', function ($http) {
		var controller = this;
		$http.get('/api/products').success(function (data) {
			controller.products = data;
		});

	})
	.controller('productsEditController', ['$http', '$scope','$routeParams','$window', function ($http, $scope,$routeParams,$window) {

		var controller = this;

		$http.get('/api/products/'+$routeParams.id).success(function (data) {
			controller.products = data;
			$scope.product = data;

		});

		controller.submitProduct = function (product) {
			$http.put('/api/products/'+$routeParams.id, {'name': product.name, 'price': product.price}).success(function (data) {
				$window.alert("Product updated successfully!");
				$window.location.href='#/products';
			});
		};


	}])
	.controller('invoiceNewController', ['$http', '$scope', '$location', function ($http, $scope, $location) {

		var controller = this;

		$scope.invoice = {discount: 0, items: [{quantity: 1, product_id: 1, price: 0}]};

		$http.get('/api/customers').success(function (data) {
			controller.customers = data;
		});

		$http.get('/api/products').success(function (data) {
			controller.products = data;
		});

		$scope.addItem = function () {
			$scope.invoice.items.push({quantity: 1, product_id: 1});
		}
		$scope.removeItem = function (item) {
			$scope.invoice.items.splice($scope.invoice.items.indexOf(item), 1);
		};
		$scope.invoiceSubTotal = function () {
			var total = 0.00;
			angular.forEach($scope.invoice.items, function (item, key) {
				if (item.product_id.price > 0) {
					total += (item.quantity * item.product_id.price);
				}
			});
			return total;
		};

		$scope.calculateDiscount = function () {
			if ($scope.invoice.discount > 0) {
				return (($scope.invoice.discount * $scope.invoiceSubTotal()) / 100);
			}
			else {
				return 0;
			}
		};


		$scope.calculateGrandTotal = function () {
			return $scope.invoiceSubTotal() - $scope.calculateDiscount();
		};


		controller.submitInvoice = function (invoice) {

			invoice.total = $scope.calculateGrandTotal();

			if(invoice.checked == 'add'){

				$http.post('/api/customers', {'name': invoice.customer_name}).success(function (data) {
					$http.post('/api/invoices', {'customer_id': data.id, 'discount': invoice.discount, 'total': invoice.total}).success(function (data) {

						angular.forEach(invoice.items, function (item, key) {
							$http.post('/api/invoices/' + data.id + '/items', {'invoice_id': data.id, 'product_id': item.product_id.id, 'quantity': item.quantity}).success(function (data2) {
								$location.path('/invoices');
							});

						});
					});	
				});
				
			}else{
				$http.post('/api/invoices', {'customer_id': invoice.customer_id, 'discount': invoice.discount, 'total': invoice.total}).success(function (data) {
					
					angular.forEach(invoice.items, function (item, key) {
						$http.post('/api/invoices/' + data.id + '/items', {'invoice_id': data.id, 'product_id': item.product_id.id, 'quantity': item.quantity}).success(function (data2) {
							$location.path('/invoices');
						});

					});
				});
			}
		};


	}]);

})();

