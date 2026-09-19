import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Dashboard } from './components/dashboard/dashboard';
import { ProductList } from './components/product/product-list/product-list';
import { ProductForm } from './components/product/product-form/product-form';
import { OrderList } from './components/order/order-list/order-list';
import { OrderForm } from './components/order/order-form/order-form';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [authGuard],
    children: [
      { path: 'products', component: ProductList },
      { path: 'products/new', component: ProductForm },
      { path: 'products/edit/:id', component: ProductForm },
      { path: 'orders', component: OrderList },
      { path: 'orders/new', component: OrderForm }
    ]
  },
  { path: '**', redirectTo: 'login' }
];