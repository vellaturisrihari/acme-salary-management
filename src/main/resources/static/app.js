(function () {
    'use strict';

    angular.module('acmeSalaryApp', [])
        .controller('SalaryController', ['$http', '$filter', '$q', function ($http, $filter, $q) {
            var vm = this;
            vm.Math = Math;
            vm.loading = true;
            vm.dashboard = { employeeCount: 0, payrollByCurrency: [], payrollByCountry: [] };
            vm.metadata = { countries: [], departments: [], currencies: [] };
            vm.page = { content: [], totalElements: 0, totalPages: 0, number: 0, size: 25 };
            vm.filters = { search: '', country: '', department: '' };
            vm.sort = { field: 'lastName', direction: 'asc' };

            vm.money = function (amount, currency) {
                if (amount === null || amount === undefined || !currency) {
                    return '—';
                }
                try {
                    return new Intl.NumberFormat(undefined, {
                        style: 'currency',
                        currency: currency,
                        maximumFractionDigits: 0
                    }).format(amount);
                } catch (error) {
                    return $filter('number')(amount, 0) + ' ' + currency;
                }
            };

            vm.search = function () {
                vm.page.number = 0;
                vm.loadEmployees();
            };

            vm.loadEmployees = function () {
                vm.loading = true;
                vm.error = '';
                $http.get('/api/employees', {
                    params: {
                        search: vm.filters.search || undefined,
                        country: vm.filters.country || undefined,
                        department: vm.filters.department || undefined,
                        page: vm.page.number,
                        size: vm.page.size,
                        sort: vm.sort.field,
                        direction: vm.sort.direction
                    }
                }).then(function (response) {
                    vm.page = response.data;
                }).catch(function (response) {
                    vm.error = vm.messageFor(response, 'Could not load employee records.');
                }).finally(function () {
                    vm.loading = false;
                });
            };

            vm.loadDashboard = function () {
                return $http.get('/api/dashboard').then(function (response) {
                    vm.dashboard = response.data;
                }).catch(function (response) {
                    vm.error = vm.messageFor(response, 'Could not load salary summaries.');
                });
            };

            vm.loadMetadata = function () {
                return $http.get('/api/metadata').then(function (response) {
                    vm.metadata = response.data;
                }).catch(function (response) {
                    vm.error = vm.messageFor(response, 'Could not load employee options.');
                });
            };

            vm.goTo = function (page) {
                if (page >= 0 && page < vm.page.totalPages) {
                    vm.page.number = page;
                    vm.loadEmployees();
                }
            };

            vm.setSort = function (field) {
                if (vm.sort.field === field) {
                    vm.sort.direction = vm.sort.direction === 'asc' ? 'desc' : 'asc';
                } else {
                    vm.sort.field = field;
                    vm.sort.direction = 'asc';
                }
                vm.page.number = 0;
                vm.loadEmployees();
            };

            vm.openCreate = function () {
                vm.editing = false;
                vm.formError = '';
                vm.form = { hireDate: new Date() };
                vm.modalOpen = true;
            };

            vm.openEdit = function (employee) {
                vm.editing = true;
                vm.formError = '';
                vm.form = angular.copy(employee);
                vm.form.hireDate = new Date(employee.hireDate + 'T00:00:00');
                vm.modalOpen = true;
            };

            vm.closeModal = function () {
                vm.modalOpen = false;
            };

            vm.save = function () {
                vm.formError = '';
                vm.saving = true;
                var payload = angular.copy(vm.form);
                payload.hireDate = $filter('date')(payload.hireDate, 'yyyy-MM-dd');
                var request = vm.editing
                    ? $http.put('/api/employees/' + vm.form.id, payload)
                    : $http.post('/api/employees', payload);
                request.then(function () {
                    vm.closeModal();
                    vm.notice = vm.editing ? 'Employee record updated.' : 'Employee added.';
                    vm.loadDashboard();
                    vm.loadMetadata().then(vm.loadEmployees);
                }).catch(function (response) {
                    vm.formError = vm.messageFor(response, 'Could not save this employee.');
                }).finally(function () {
                    vm.saving = false;
                });
            };

            vm.remove = function (employee) {
                if (!window.confirm('Delete the salary record for ' + employee.firstName + ' ' + employee.lastName + '?')) {
                    return;
                }
                vm.error = '';
                $http.delete('/api/employees/' + employee.id).then(function () {
                    vm.notice = 'Employee salary record deleted.';
                    vm.loadDashboard();
                    vm.loadMetadata().then(vm.loadEmployees);
                }).catch(function (response) {
                    vm.error = vm.messageFor(response, 'Could not delete this employee.');
                });
            };

            vm.messageFor = function (response, fallback) {
                if (response.data && response.data.error) {
                    return response.data.error;
                }
                if (response.data && typeof response.data === 'object') {
                    var fields = Object.keys(response.data).map(function (key) {
                        return response.data[key];
                    });
                    if (fields.length) {
                        return fields.join(' ');
                    }
                }
                return fallback;
            };

            $q.all([vm.loadDashboard(), vm.loadMetadata()]).finally(function () {
                vm.loadEmployees();
            });
        }]);
}());
